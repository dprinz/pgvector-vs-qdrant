package demo.bench;

import com.pgvector.PGhalfvec;
import com.pgvector.PGvector;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.QueryPoints;
import io.qdrant.client.grpc.Points.SearchParams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.qdrant.client.QueryFactory.nearest;

/**
 * Throughput with several concurrent clients, unfiltered. Single-threaded latency alone favours an engine that
 * parallelises one query internally; under load every core is busy anyway.
 */
final class Throughput {

    interface Searcher extends AutoCloseable {
        long[] search(float[] query) throws Exception;

        @Override
        default void close() throws Exception {
        }
    }

    interface Factory {
        Searcher open() throws Exception;
    }

    private Throughput() {
    }

    static Factory pg(Pg.Type type, int efSearch) {
        return () -> {
            Connection c = Pg.connect();
            Pg.exec(c, "SET hnsw.ef_search = " + efSearch);
            PreparedStatement ps = c.prepareStatement("SELECT id FROM " + type.table
                    + " ORDER BY embedding <#> ? LIMIT " + Data.K);
            return new Searcher() {
                @Override
                public long[] search(float[] query) throws Exception {
                    ps.setObject(1, type == Pg.Type.VECTOR ? new PGvector(query) : new PGhalfvec(query));
                    long[] ids = new long[Data.K];
                    int n = 0;
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            ids[n++] = rs.getInt(1);
                        }
                    }
                    return java.util.Arrays.copyOf(ids, n);
                }

                @Override
                public void close() throws Exception {
                    ps.close();
                    c.close();
                }
            };
        };
    }

    /** One shared gRPC client: it multiplexes concurrent requests over one channel. */
    static Factory qdrant(QdrantClient client, int ef) {
        return () -> query -> client.queryAsync(QueryPoints.newBuilder()
                        .setCollectionName(QdrantBench.COLLECTION)
                        .setQuery(nearest(query))
                        .setLimit(Data.K)
                        .setParams(SearchParams.newBuilder().setHnswEf(ef).build())
                        .build()).get().stream()
                .mapToLong(p -> p.getId().getNum()).toArray();
    }

    static void run(String engine, String setting, Factory factory, int threads) throws Exception {
        float[][] queries = Data.read(Data.QUERIES);
        int[][] truth = Data.truth(Data.Filter.NONE);
        // warm-up, single-threaded
        try (Searcher s = factory.open()) {
            for (float[] q : queries) {
                s.search(q);
            }
        }
        List<Future<double[]>> parts = new ArrayList<>();
        long start = System.nanoTime();
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                int first = t;
                parts.add(pool.submit(() -> {
                    double recall = 0;
                    int count = 0;
                    try (Searcher s = factory.open()) {
                        // three passes so that the run lasts long enough to be measured
                        for (int pass = 0; pass < 3; pass++) {
                            for (int q = first; q < queries.length; q += threads) {
                                recall += Results.recall(truth[q], s.search(queries[q]));
                                count++;
                            }
                        }
                    }
                    return new double[]{recall, count};
                }));
            }
        }
        double seconds = (System.nanoTime() - start) / 1e9;
        double recall = 0;
        double count = 0;
        for (Future<double[]> f : parts) {
            recall += f.get()[0];
            count += f.get()[1];
        }
        Map<String, Object> r = Results.record("throughput");
        r.put("engine", engine);
        r.put("setting", setting);
        r.put("threads", threads);
        r.put("queries", (long) count);
        r.put("seconds", seconds);
        r.put("qps", count / seconds);
        r.put("recall_at_10", recall / count);
        Results.write(r);
    }
}
