package demo.rag;

import com.pgvector.PGvector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** All SQL of the app. Every value is a bind parameter; nothing is concatenated into a statement. */
@ApplicationScoped
public class KnowledgeRepository {

    public enum Mode { VECTOR, HYBRID }

    /**
     * How the query vector is bound. TYPED sends a PGvector, i.e. a parameter of type vector. TEXT sends a string and
     * casts it in SQL with ?::vector. TEXT exists only to measure the difference, see the evaluation.
     */
    public enum Binding { TYPED, TEXT }

    /** Candidates per list before the fusion and the RRF constant. */
    static final int CANDIDATES = 50;
    static final int RRF_K = 60;

    private final DataSource dataSource;

    public KnowledgeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Document id to indexed version. */
    public Map<String, String> versions() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, version FROM document");
             ResultSet rs = ps.executeQuery()) {
            Map<String, String> versions = new HashMap<>();
            while (rs.next()) {
                versions.put(rs.getString(1), rs.getString(2));
            }
            return versions;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Replaces a document and all its chunks in one transaction. A reader sees either the old or the new version,
     * never a mix, and there is no second store that could drift apart.
     */
    @Transactional
    public void replace(Manifest.Entry entry, List<Chunker.Chunk> chunks, List<float[]> embeddings) {
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("""
                    INSERT INTO document (id, title, topic, source, version) VALUES (?, ?, ?, ?, ?)
                    ON CONFLICT (id) DO UPDATE SET title = excluded.title, topic = excluded.topic,
                        source = excluded.source, version = excluded.version""")) {
                ps.setString(1, entry.id());
                ps.setString(2, entry.title());
                ps.setString(3, entry.topic());
                ps.setString(4, entry.source());
                ps.setString(5, entry.version());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM chunk WHERE document_id = ?")) {
                ps.setString(1, entry.id());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO chunk (document_id, position, section, text, embedding) VALUES (?, ?, ?, ?, ?)")) {
                for (int i = 0; i < chunks.size(); i++) {
                    Chunker.Chunk chunk = chunks.get(i);
                    ps.setString(1, entry.id());
                    ps.setInt(2, chunk.position());
                    ps.setString(3, chunk.section());
                    ps.setString(4, chunk.text());
                    ps.setObject(5, new PGvector(embeddings.get(i)));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Hit> search(float[] query, String question, String topic, int k, Mode mode) {
        try (Connection c = dataSource.getConnection()) {
            return search(c, query, question, topic, k, mode, Binding.TYPED);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Same search on a given connection; the evaluation uses it to control session settings. */
    public static List<Hit> search(Connection c, float[] query, String question, String topic, int k, Mode mode,
                                   Binding binding) throws SQLException {
        String vector = binding == Binding.TYPED ? "?" : "?::vector";
        String filter = topic == null ? "" : "WHERE d.topic = ?";
        List<Object> params = new ArrayList<>();
        Object vectorParam = binding == Binding.TYPED ? new PGvector(query) : new PGvector(query).toString();
        String sql;
        String fullText = mode == Mode.HYBRID ? orQuery(question) : "";
        if (fullText.isEmpty()) {
            // Cosine distance <=>. ORDER BY on the distance is what an HNSW index can serve.
            sql = """
                    SELECT c.document_id, d.title, c.section, c.text, c.embedding <=> %s AS score
                    FROM chunk c JOIN document d ON d.id = c.document_id
                    %s
                    ORDER BY score
                    LIMIT ?""".formatted(vector, filter);
            params.add(vectorParam);
            if (topic != null) {
                params.add(topic);
            }
            params.add(k);
        } else {
            // Hybrid: the best CANDIDATES of the vector search and of the full-text search, fused with Reciprocal
            // Rank Fusion: score = sum over both lists of 1 / (RRF_K + rank). One statement, one snapshot.
            sql = """
                    WITH vec AS (
                        SELECT c.document_id, c.position, row_number() OVER (ORDER BY c.embedding <=> %1$s) AS rank
                        FROM chunk c JOIN document d ON d.id = c.document_id
                        %2$s
                        ORDER BY c.embedding <=> %1$s
                        LIMIT %3$d
                    ), txt AS (
                        SELECT c.document_id, c.position,
                               row_number() OVER (ORDER BY ts_rank_cd(c.tsv, q) DESC) AS rank
                        FROM chunk c JOIN document d ON d.id = c.document_id,
                             websearch_to_tsquery('simple', ?) q
                        WHERE c.tsv @@ q %4$s
                        ORDER BY ts_rank_cd(c.tsv, q) DESC
                        LIMIT %3$d
                    ), fused AS (
                        SELECT document_id, position, sum(1.0 / (%5$d + rank)) AS score
                        FROM (SELECT * FROM vec UNION ALL SELECT * FROM txt) ranked
                        GROUP BY document_id, position
                    )
                    SELECT f.document_id, d.title, c.section, c.text, f.score
                    FROM fused f
                    JOIN chunk c ON c.document_id = f.document_id AND c.position = f.position
                    JOIN document d ON d.id = f.document_id
                    ORDER BY f.score DESC
                    LIMIT ?""".formatted(vector, filter, CANDIDATES, topic == null ? "" : "AND d.topic = ?",
                    RRF_K);
            // Placeholders in text order: rank vector, topic filter, order vector, full text, topic filter, limit.
            params.add(vectorParam);
            if (topic != null) {
                params.add(topic);
            }
            params.add(vectorParam);
            params.add(fullText);
            if (topic != null) {
                params.add(topic);
            }
            params.add(k);
        }
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            List<Hit> hits = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hits.add(new Hit(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getDouble(5)));
                }
            }
            return hits;
        }
    }

    /** "Was ist ein Apochromat?" becomes "was or ist or ein or apochromat" for websearch_to_tsquery. */
    static String orQuery(String question) {
        return Arrays.stream(question.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+"))
                .filter(word -> !word.isEmpty())
                .distinct()
                .collect(Collectors.joining(" or "));
    }
}
