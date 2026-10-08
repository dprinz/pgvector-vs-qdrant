package demo.rag;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.rag.KnowledgeRepository.Binding;
import demo.rag.KnowledgeRepository.Mode;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Retrieval evaluation over evaluation/questions.json: 20 German questions with annotated relevant sections and 3
 * questions without evidence in the corpus. Writes build/reports/evaluation.md.
 * <p>
 * Run with ./gradlew :rag-app:evaluate (not part of ./gradlew test).
 */
@QuarkusTest
@Tag("evaluation")
class RetrievalEvaluation {

    record Relevant(String documentId, String section) {
    }

    record Question(String id, String question, List<Relevant> relevant) {
    }

    record Questions(String version, int topK, List<Question> questions) {
    }

    record Quality(double hitRate, double mrr) {
    }

    private static final int ROUNDS = 5;

    @Inject
    IngestService ingest;
    @Inject
    Embedder embedder;
    @Inject
    DataSource dataSource;
    @Inject
    ObjectMapper mapper;

    private final StringBuilder report = new StringBuilder();

    @Test
    void evaluate() throws Exception {
        long t0 = System.nanoTime();
        IngestService.Result result = ingest.ingest();
        double ingestSeconds = (System.nanoTime() - t0) / 1e9;

        Questions set;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("evaluation/questions.json")) {
            set = mapper.readValue(in, Questions.class);
        }
        List<Question> questions = set.questions();
        int k = set.topK();
        List<Question> withEvidence = questions.stream().filter(q -> !q.relevant().isEmpty()).toList();

        // Embedding of the questions, warm-up first
        embedder.query("warm-up");
        List<Double> embedMs = new ArrayList<>();
        List<float[]> vectors = new ArrayList<>();
        for (Question q : questions) {
            long s = System.nanoTime();
            vectors.add(embedder.query(q.question()));
            embedMs.add((System.nanoTime() - s) / 1e6);
        }

        line("# Retrieval evaluation");
        line("");
        line("Corpus: %d documents indexed, %d chunks, ingest %.1f s (chunking, embedding, writing).",
                result.indexed() + result.skipped(), result.chunks(), ingestSeconds);
        line("Questions: %d (%d with evidence). Top-%d, cosine distance. Embedding of a question: p50 %.1f ms, "
                + "p95 %.1f ms.", questions.size(), withEvidence.size(), k, pct(embedMs, 50), pct(embedMs, 95));
        line("Java %s, %s.", System.getProperty("java.version"), System.getProperty("os.arch"));
        line("");

        try (Connection c = dataSource.getConnection()) {
            line("PostgreSQL: %s, pgvector %s.", scalar(c, "SHOW server_version"),
                    scalar(c, "SELECT extversion FROM pg_extension WHERE extname = 'vector'"));
            line("");

            // 1. Quality and latency: exact vector search against hybrid search
            line("## Vector search against hybrid search (exact, no index)");
            line("");
            line("| Search | Hit Rate@%d | MRR@%d | Latency p50 / p95 ms |", k, k);
            line("|---|---|---|---|");
            List<List<Hit>> exact = run(c, questions, vectors, k, Mode.VECTOR, Binding.TYPED);
            Quality vq = quality(withEvidence, questions, exact);
            List<Double> vl = latency(c, questions, vectors, k, Mode.VECTOR, Binding.TYPED);
            line("| Vector (cosine) | %.2f | %.2f | %.2f / %.2f |", vq.hitRate(), vq.mrr(), pct(vl, 50), pct(vl, 95));
            List<List<Hit>> hybrid = run(c, questions, vectors, k, Mode.HYBRID, Binding.TYPED);
            Quality hq = quality(withEvidence, questions, hybrid);
            List<Double> hl = latency(c, questions, vectors, k, Mode.HYBRID, Binding.TYPED);
            line("| Hybrid (vector + full text 'simple', RRF k=%d, %d candidates each) | %.2f | %.2f | "
                            + "%.2f / %.2f |", KnowledgeRepository.RRF_K, KnowledgeRepository.CANDIDATES,
                    hq.hitRate(), hq.mrr(), pct(hl, 50), pct(hl, 95));
            line("");
            line("### Per question (rank of the first relevant hit, `-` = none in the top %d)", k);
            line("");
            line("| Question | Vector | Hybrid | Vector: top-1 (distance) | Hybrid: top-1 |");
            line("|---|---|---|---|---|");
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                Hit v = exact.get(i).getFirst();
                Hit h = hybrid.get(i).getFirst();
                line("| %s %s | %s | %s | %s / %s (%.3f) | %s / %s |", q.id(), q.question(),
                        rankText(q, exact.get(i)), rankText(q, hybrid.get(i)), v.documentId(), v.section(),
                        v.score(), h.documentId(), h.section());
            }
            line("");

            // Why does the hybrid search lose a hit? The top of the full-text list for those questions.
            line("### Full-text list where the hybrid search is worse (top 3 of `websearch_to_tsquery('simple', ...)`)");
            line("");
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                int v = rank(q, exact.get(i));
                int h = rank(q, hybrid.get(i));
                if (!q.relevant().isEmpty() && v > 0 && (h == 0 || h > v)) {
                    line("- %s %s (query `%s`): %s", q.id(), q.question(), KnowledgeRepository.orQuery(q.question()),
                            fullText(c, q.question()));
                }
            }
            line("");

            // 2. Distance of the best hit: can a threshold detect "no source"?
            List<Double> with = new ArrayList<>();
            List<Double> without = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                (questions.get(i).relevant().isEmpty() ? without : with).add(exact.get(i).getFirst().score());
            }
            line("## Distance of the best hit");
            line("");
            line("With evidence: min %.3f, median %.3f, max %.3f. Without evidence: %s.", min(with), pct(with, 50),
                    max(with), without.stream().map(d -> String.format(Locale.ROOT, "%.3f", d)).toList());
            line("");

            // 3. Binding of the query vector
            bindingSection(c, vectors.getFirst(), k);

            // 4. HNSW against exact search
            hnswSection(c, questions, vectors, k, exact);
        }

        Path out = Path.of("build/reports/evaluation.md");
        Files.createDirectories(out.getParent());
        Files.writeString(out, report);
        System.out.println(report);
    }

    private void bindingSection(Connection c, float[] vector, int k) throws SQLException {
        line("## Binding the query vector: typed parameter against `?::vector`");
        line("");
        line("The same question 30 times in a row on one connection. pgJDBC prepares a statement on the server after"
                + " 5 executions (prepareThreshold); from then on PostgreSQL decides per execution between a custom"
                + " plan and a generic plan (plan_cache_mode = auto).");
        line("");
        int runs = 30;
        // The app's statement has LIMIT ? as a second parameter; the minimal statement has a constant LIMIT.
        String minimal = "SELECT document_id, section FROM chunk ORDER BY embedding <=> %s LIMIT " + k;
        String[] names = {"app SQL, typed", "app SQL, text", "minimal SQL, typed", "minimal SQL, text"};
        double[][] series = new double[names.length][runs];
        List<String> plans = new ArrayList<>();
        for (int variant = 0; variant < names.length; variant++) {
            // fresh statements: discard the server-side prepared statements of earlier runs
            exec(c, "DISCARD ALL");
            Binding binding = variant % 2 == 0 ? Binding.TYPED : Binding.TEXT;
            for (int i = 0; i < runs; i++) {
                long s = System.nanoTime();
                if (variant < 2) {
                    KnowledgeRepository.search(c, vector, "", null, k, Mode.VECTOR, binding);
                } else {
                    String sql = minimal.formatted(binding == Binding.TYPED ? "?" : "?::vector");
                    try (java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
                        com.pgvector.PGvector v = new com.pgvector.PGvector(vector);
                        ps.setObject(1, binding == Binding.TYPED ? v : v.toString());
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                rs.getString(1);
                            }
                        }
                    }
                }
                series[variant][i] = (System.nanoTime() - s) / 1e6;
            }
            // How did PostgreSQL plan the server-side prepared statement?
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("""
                    SELECT parameter_types::text, generic_plans, custom_plans FROM pg_prepared_statements
                    WHERE statement LIKE '%embedding <=>%' AND NOT from_sql""")) {
                while (rs.next()) {
                    plans.add("%s: parameter types %s, generic plans %d, custom plans %d".formatted(names[variant],
                            rs.getString(1), rs.getLong(2), rs.getLong(3)));
                }
            }
        }
        line("| Execution | %s |", String.join(" | ", names));
        line("|---|---|---|---|---|");
        for (int i = 0; i < runs; i++) {
            if (i < 12 || i % 5 == 4) {
                line("| %d | %.2f ms | %.2f ms | %.2f ms | %.2f ms |", i + 1, series[0][i], series[1][i],
                        series[2][i], series[3][i]);
            }
        }
        line("");
        line("Median of executions 11 to 30: %.2f / %.2f / %.2f / %.2f ms.", median(series[0], 10),
                median(series[1], 10), median(series[2], 10), median(series[3], 10));
        line("");
        plans.forEach(p -> line("- %s", p));
        line("");
        line("Generic plans (prepared like pgJDBC does: the typed vector as `vector`, the string as `varchar`):");
        line("");
        String base = "SELECT c.document_id FROM chunk c ORDER BY c.embedding <=> %s LIMIT 5";
        String literal = "'" + new com.pgvector.PGvector(vector) + "'";
        exec(c, "SET plan_cache_mode = force_generic_plan");
        exec(c, "PREPARE typed(vector) AS " + base.formatted("$1"));
        exec(c, "PREPARE text(varchar) AS " + base.formatted("$1::vector"));
        line("```text");
        line("-- typed: PREPARE typed(vector) AS ... ORDER BY c.embedding <=> $1");
        explain(c, "EXPLAIN (ANALYZE, COSTS OFF, VERBOSE) EXECUTE typed(" + literal + ")");
        line("-- text: PREPARE text(varchar) AS ... ORDER BY c.embedding <=> $1::vector");
        explain(c, "EXPLAIN (ANALYZE, COSTS OFF, VERBOSE) EXECUTE text(" + literal + ")");
        line("```");
        line("");
        exec(c, "DEALLOCATE typed");
        exec(c, "DEALLOCATE text");
        exec(c, "RESET plan_cache_mode");
    }

    private void hnswSection(Connection c, List<Question> questions, List<float[]> vectors, int k,
                             List<List<Hit>> exact) throws SQLException {
        line("## HNSW index against exact search");
        line("");
        long s = System.nanoTime();
        exec(c, "CREATE INDEX chunk_embedding_idx ON chunk USING hnsw (embedding vector_cosine_ops)");
        double buildMs = (System.nanoTime() - s) / 1e6;
        exec(c, "ANALYZE chunk");
        // With a few hundred rows the planner prefers the sequential scan; force the index for the comparison.
        exec(c, "SET enable_seqscan = off");
        try {
            List<List<Hit>> approx = run(c, questions, vectors, k, Mode.VECTOR, Binding.TYPED);
            List<Double> al = latency(c, questions, vectors, k, Mode.VECTOR, Binding.TYPED);
            int same = 0;
            for (int i = 0; i < questions.size(); i++) {
                if (ids(approx.get(i)).equals(ids(exact.get(i)))) {
                    same++;
                }
            }
            exec(c, "RESET enable_seqscan");
            exec(c, "SET enable_indexscan = off");
            List<Double> el = latency(c, questions, vectors, k, Mode.VECTOR, Binding.TYPED);
            exec(c, "RESET enable_indexscan");
            line("Index build %.0f ms (defaults m = 16, ef_construction = 64), size %s. ef_search = 40 (default).",
                    buildMs, scalar(c, "SELECT pg_size_pretty(pg_relation_size('chunk_embedding_idx'))"));
            line("");
            line("| Search | p50 ms | p95 ms |");
            line("|---|---|---|");
            line("| exact (sequential scan) | %.2f | %.2f |", pct(el, 50), pct(el, 95));
            line("| HNSW | %.2f | %.2f |", pct(al, 50), pct(al, 95));
            line("");
            line("Same top-%d in the same order: %d of %d questions.", k, same, questions.size());
            line("");
            exec(c, "SET enable_seqscan = off");
            line("```text");
            explain(c, "EXPLAIN (ANALYZE, BUFFERS, COSTS OFF) SELECT c.document_id, c.embedding <=> "
                    + "(SELECT embedding FROM chunk LIMIT 1) AS score FROM chunk c ORDER BY score LIMIT 5");
            line("```");
            line("");
        } finally {
            exec(c, "RESET enable_seqscan");
            exec(c, "DROP INDEX IF EXISTS chunk_embedding_idx");
        }
    }

    // --- helpers ---

    private static List<List<Hit>> run(Connection c, List<Question> questions, List<float[]> vectors, int k,
                                       Mode mode, Binding binding) throws SQLException {
        List<List<Hit>> all = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            all.add(KnowledgeRepository.search(c, vectors.get(i), questions.get(i).question(), null, k, mode,
                    binding));
        }
        return all;
    }

    /** One warm-up pass, then ROUNDS passes over all questions. */
    private static List<Double> latency(Connection c, List<Question> questions, List<float[]> vectors, int k,
                                        Mode mode, Binding binding) throws SQLException {
        run(c, questions, vectors, k, mode, binding);
        List<Double> ms = new ArrayList<>();
        for (int r = 0; r < ROUNDS; r++) {
            for (int i = 0; i < questions.size(); i++) {
                long s = System.nanoTime();
                KnowledgeRepository.search(c, vectors.get(i), questions.get(i).question(), null, k, mode, binding);
                ms.add((System.nanoTime() - s) / 1e6);
            }
        }
        return ms;
    }

    private static Quality quality(List<Question> withEvidence, List<Question> all, List<List<Hit>> hits) {
        double found = 0;
        double reciprocal = 0;
        for (Question q : withEvidence) {
            int rank = rank(q, hits.get(all.indexOf(q)));
            if (rank > 0) {
                found++;
                reciprocal += 1.0 / rank;
            }
        }
        return new Quality(found / withEvidence.size(), reciprocal / withEvidence.size());
    }

    /** 1-based rank of the first hit whose document and section are annotated as relevant, 0 if none. */
    private static int rank(Question q, List<Hit> hits) {
        for (int i = 0; i < hits.size(); i++) {
            Hit h = hits.get(i);
            if (q.relevant().stream().anyMatch(r -> r.documentId().equals(h.documentId())
                    && r.section().equals(h.section()))) {
                return i + 1;
            }
        }
        return 0;
    }

    private static String rankText(Question q, List<Hit> hits) {
        if (q.relevant().isEmpty()) {
            return "(no evidence)";
        }
        int rank = rank(q, hits);
        return rank == 0 ? "-" : String.valueOf(rank);
    }

    private static List<String> ids(List<Hit> hits) {
        return hits.stream().map(h -> h.documentId() + "/" + h.section() + "/" + h.text().hashCode()).toList();
    }

    private static double pct(List<Double> values, int p) {
        double[] sorted = values.stream().mapToDouble(Double::doubleValue).sorted().toArray();
        int index = (int) Math.ceil(p / 100.0 * sorted.length) - 1;
        return sorted[Math.max(0, index)];
    }

    private static double median(double[] series, int from) {
        double[] tail = Arrays.copyOfRange(series, from, series.length);
        Arrays.sort(tail);
        return tail[tail.length / 2];
    }

    private static double min(List<Double> v) {
        return v.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
    }

    private static double max(List<Double> v) {
        return v.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
    }

    private static String fullText(Connection c, String question) throws SQLException {
        try (java.sql.PreparedStatement ps = c.prepareStatement("""
                SELECT c.document_id, c.section, ts_rank_cd(c.tsv, q) AS rank,
                       (SELECT string_agg(DISTINCT lexeme, ' ') FROM unnest(c.tsv) u
                        WHERE u.lexeme = ANY (string_to_array(?, ' '))) AS matched
                FROM chunk c, websearch_to_tsquery('simple', ?) q
                WHERE c.tsv @@ q ORDER BY rank DESC LIMIT 3""")) {
            ps.setString(1, KnowledgeRepository.orQuery(question).replace(" or ", " "));
            ps.setString(2, KnowledgeRepository.orQuery(question));
            List<String> hits = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hits.add(String.format(Locale.ROOT, "%s / %s (%.2f, matched: %s)", rs.getString(1),
                            rs.getString(2), rs.getDouble(3), rs.getString(4)));
                }
            }
            return String.join("; ", hits);
        }
    }

    private static void exec(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    private static String scalar(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getString(1);
        }
    }

    private void explain(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                line("%s", rs.getString(1));
            }
        }
    }

    private void line(String format, Object... args) {
        report.append(String.format(Locale.ROOT, format, args)).append('\n');
    }
}
