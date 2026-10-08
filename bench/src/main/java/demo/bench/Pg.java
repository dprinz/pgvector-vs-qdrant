package demo.bench;

import com.pgvector.PGhalfvec;
import com.pgvector.PGvector;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** pgvector: load with binary COPY, build HNSW, query with and without filter. */
final class Pg {

    static final String URL = System.getenv().getOrDefault("PG_URL", "jdbc:postgresql://localhost:5432/bench");

    /** Column type: vector (float32) or halfvec (float16). */
    enum Type {
        VECTOR("items", "vector", "vector_ip_ops"), HALFVEC("items_half", "halfvec", "halfvec_ip_ops");

        final String table;
        final String sqlType;
        final String opclass;

        Type(String table, String sqlType, String opclass) {
            this.table = table;
            this.sqlType = sqlType;
            this.opclass = opclass;
        }
    }

    private Pg() {
    }

    static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, "bench", "bench");
    }

    /** items (vector) via binary COPY, then items_half (halfvec) as a cast copy of it. */
    static void load() throws Exception {
        int n = Data.count(Data.BASE);
        try (Connection c = connect()) {
            exec(c, "CREATE EXTENSION IF NOT EXISTS vector");
            exec(c, "DROP TABLE IF EXISTS items, items_half");
            exec(c, "CREATE TABLE items (id integer PRIMARY KEY, category smallint NOT NULL, "
                    + "embedding vector(" + Data.DIM + ") NOT NULL)");
            long start = System.nanoTime();
            CopyIn copy = c.unwrap(PGConnection.class).getCopyAPI()
                    .copyIn("COPY items (id, category, embedding) FROM STDIN WITH (FORMAT BINARY)");
            // Binary COPY: header, then per row the field count and each field as length + value (big-endian).
            // A vector is int16 dimensions, int16 unused, then float32 values.
            int rowBytes = 2 + (4 + 4) + (4 + 2) + (4 + 4 + 4 * Data.DIM);
            ByteBuffer row = ByteBuffer.allocate(rowBytes).order(ByteOrder.BIG_ENDIAN);
            byte[] header = {'P', 'G', 'C', 'O', 'P', 'Y', '\n', (byte) 0xff, '\r', '\n', 0, 0, 0, 0, 0, 0, 0, 0, 0};
            copy.writeToCopy(header, 0, header.length);
            try (Data.VectorReader reader = new Data.VectorReader(Data.BASE)) {
                for (int id = 0; id < n; id++) {
                    float[] v = reader.next();
                    row.clear();
                    row.putShort((short) 3);
                    row.putInt(4).putInt(id);
                    row.putInt(2).putShort((short) Data.category(id));
                    row.putInt(4 + 4 * Data.DIM).putShort((short) Data.DIM).putShort((short) 0);
                    for (float f : v) {
                        row.putFloat(f);
                    }
                    copy.writeToCopy(row.array(), 0, rowBytes);
                    if (id % 100_000 == 0) {
                        System.out.printf("%,d rows%n", id);
                    }
                }
            }
            byte[] trailer = {(byte) 0xff, (byte) 0xff};
            copy.writeToCopy(trailer, 0, 2);
            long rows = copy.endCopy();
            double copySeconds = (System.nanoTime() - start) / 1e9;

            start = System.nanoTime();
            exec(c, "CREATE TABLE items_half AS SELECT id, category, embedding::halfvec(" + Data.DIM
                    + ") AS embedding FROM items");
            exec(c, "ALTER TABLE items_half ADD PRIMARY KEY (id)");
            double halfSeconds = (System.nanoTime() - start) / 1e9;
            exec(c, "VACUUM ANALYZE items");
            exec(c, "VACUUM ANALYZE items_half");

            Map<String, Object> r = Results.record("pg-load");
            r.put("rows", rows);
            r.put("copy_s", copySeconds);
            r.put("halfvec_copy_s", halfSeconds);
            r.put("items_size", scalar(c, "SELECT pg_size_pretty(pg_total_relation_size('items'))"));
            r.put("items_half_size", scalar(c, "SELECT pg_size_pretty(pg_total_relation_size('items_half'))"));
            r.put("server", scalar(c, "SHOW server_version"));
            r.put("pgvector", scalar(c, "SELECT extversion FROM pg_extension WHERE extname = 'vector'"));
            Results.write(r);
        }
    }

    static void index(Type type, int m, int efConstruction, int workers, String memory) throws Exception {
        try (Connection c = connect()) {
            exec(c, "DROP INDEX IF EXISTS " + type.table + "_hnsw");
            exec(c, "SET maintenance_work_mem = '" + memory + "'");
            exec(c, "SET max_parallel_maintenance_workers = " + workers);
            long start = System.nanoTime();
            List<String> notices = new ArrayList<>();
            try (Statement st = c.createStatement()) {
                st.execute("CREATE INDEX " + type.table + "_hnsw ON " + type.table + " USING hnsw (embedding "
                        + type.opclass + ") WITH (m = " + m + ", ef_construction = " + efConstruction + ")");
                for (SQLWarning w = st.getWarnings(); w != null; w = w.getNextWarning()) {
                    notices.add(w.getMessage());
                }
            }
            double seconds = (System.nanoTime() - start) / 1e9;
            Map<String, Object> r = Results.record("pg-index");
            r.put("type", type.sqlType);
            r.put("m", m);
            r.put("ef_construction", efConstruction);
            r.put("workers", workers);
            r.put("maintenance_work_mem", memory);
            r.put("build_s", seconds);
            r.put("index_size", scalar(c, "SELECT pg_size_pretty(pg_relation_size('" + type.table + "_hnsw'))"));
            r.put("notices", String.join(" | ", notices));
            Results.write(r);
        }
    }

    static void btree(boolean create) throws Exception {
        try (Connection c = connect()) {
            for (Type t : Type.values()) {
                exec(c, create
                        ? "CREATE INDEX IF NOT EXISTS " + t.table + "_category ON " + t.table + " (category)"
                        : "DROP INDEX IF EXISTS " + t.table + "_category");
                exec(c, "ANALYZE " + t.table);
            }
        }
    }

    /**
     * Runs all queries once as warm-up and once measured, single-threaded, for one setting.
     * iterative: off, relaxed_order or strict_order (hnsw.iterative_scan, pgvector 0.8).
     */
    static void query(Type type, int efSearch, Data.Filter filter, String iterative, String label)
            throws Exception {
        float[][] queries = Data.read(Data.QUERIES);
        int[][] truth = Data.truth(filter);
        String where = filter == Data.Filter.NONE ? "" : "WHERE category < ? ";
        String sql = "SELECT id FROM " + type.table + " " + where + "ORDER BY embedding <#> ? LIMIT " + Data.K;
        try (Connection c = connect()) {
            exec(c, "SET hnsw.ef_search = " + efSearch);
            exec(c, "SET hnsw.iterative_scan = " + iterative);
            String plan = plan(c, type, sql, filter, queries[0]);
            double[] ms = new double[queries.length];
            double recallSum = 0;
            int short_ = 0;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (int pass = 0; pass < 2; pass++) {
                    for (int q = 0; q < queries.length; q++) {
                        bind(ps, type, filter, queries[q]);
                        long start = System.nanoTime();
                        long[] ids = new long[Data.K];
                        int count = 0;
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                ids[count++] = rs.getInt(1);
                            }
                        }
                        double elapsed = (System.nanoTime() - start) / 1e6;
                        if (pass == 1) {
                            ms[q] = elapsed;
                            long[] found = java.util.Arrays.copyOf(ids, count);
                            recallSum += Results.recall(truth[q], found);
                            if (count < Data.K) {
                                short_++;
                            }
                        }
                    }
                }
            }
            Map<String, Object> r = Results.record("pg-query");
            r.put("label", label);
            r.put("type", type.sqlType);
            r.put("filter", filter.label);
            r.put("ef_search", efSearch);
            r.put("iterative_scan", iterative);
            r.put("recall_at_10", recallSum / queries.length);
            r.put("fewer_than_10", short_);
            Results.latency(r, ms);
            r.put("plan", plan);
            Results.write(r);
        }
    }

    private static void bind(PreparedStatement ps, Type type, Data.Filter filter, float[] query) throws SQLException {
        int i = 1;
        if (filter != Data.Filter.NONE) {
            ps.setInt(i++, filter.bound);
        }
        ps.setObject(i, type == Type.VECTOR ? new PGvector(query) : new PGhalfvec(query));
    }

    /** Top node types of the plan for the first query, e.g. "Limit > Index Scan using items_hnsw". */
    private static String plan(Connection c, Type type, String sql, Data.Filter filter, float[] query)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("EXPLAIN (COSTS OFF) " + sql)) {
            bind(ps, type, filter, query);
            List<String> nodes = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String line = rs.getString(1).strip();
                    if (!line.startsWith("Order By") && !line.startsWith("Filter") && !line.startsWith("Sort Key")
                            && !line.startsWith("Index Cond") && !line.startsWith("Recheck")) {
                        nodes.add(line.replace("->  ", ""));
                    }
                }
            }
            return String.join(" > ", nodes);
        }
    }

    static void exec(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    static String scalar(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getString(1);
        }
    }
}
