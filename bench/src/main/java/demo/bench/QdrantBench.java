package demo.bench;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.CollectionInfo;
import io.qdrant.client.grpc.Collections.CollectionStatus;
import io.qdrant.client.grpc.Collections.CreateCollection;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.HnswConfigDiff;
import io.qdrant.client.grpc.Collections.PayloadSchemaType;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Collections.VectorsConfig;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.Common.Range;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.QueryPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchParams;
import io.qdrant.client.grpc.Points.UpsertPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static io.qdrant.client.ConditionFactory.range;
import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

/** Qdrant over gRPC: same vectors, same HNSW parameters, payload index on category before the upload. */
final class QdrantBench {

    static final String COLLECTION = "items";
    static final String HOST = System.getenv().getOrDefault("QDRANT_HOST", "localhost");

    private QdrantBench() {
    }

    static QdrantClient connect() {
        return new QdrantClient(QdrantGrpcClient.newBuilder(HOST, 6334, false).build());
    }

    static void load(int m, int efConstruct) throws Exception {
        int n = Data.count(Data.BASE);
        try (QdrantClient client = connect()) {
            if (client.collectionExistsAsync(COLLECTION).get()) {
                client.deleteCollectionAsync(COLLECTION).get();
            }
            client.createCollectionAsync(CreateCollection.newBuilder()
                    .setCollectionName(COLLECTION)
                    .setVectorsConfig(VectorsConfig.newBuilder().setParams(VectorParams.newBuilder()
                            .setSize(Data.DIM).setDistance(Distance.Dot).build()).build())
                    .setHnswConfig(HnswConfigDiff.newBuilder().setM(m).setEfConstruct(efConstruct).build())
                    .build()).get();
            // Created before the upload so that the HNSW build knows the field (filterable HNSW).
            client.createPayloadIndexAsync(COLLECTION, "category", PayloadSchemaType.Integer, null, true, null,
                    null).get();

            long start = System.nanoTime();
            int batch = 500;
            List<Future<?>> inFlight = new ArrayList<>();
            try (Data.VectorReader reader = new Data.VectorReader(Data.BASE)) {
                List<PointStruct> points = new ArrayList<>(batch);
                for (int i = 0; i < n; i++) {
                    points.add(PointStruct.newBuilder()
                            .setId(id(i))
                            .setVectors(vectors(reader.next()))
                            .putAllPayload(Map.of("category", value(Data.category(i))))
                            .build());
                    if (points.size() == batch || i == n - 1) {
                        inFlight.add(client.upsertAsync(UpsertPoints.newBuilder().setCollectionName(COLLECTION)
                                .addAllPoints(points).setWait(false).build()));
                        points = new ArrayList<>(batch);
                        // at most 8 requests in flight
                        if (inFlight.size() >= 8) {
                            inFlight.removeFirst().get();
                        }
                    }
                    if (i % 100_000 == 0) {
                        System.out.printf("%,d points%n", i);
                    }
                }
            }
            for (Future<?> f : inFlight) {
                f.get();
            }
            double uploadSeconds = (System.nanoTime() - start) / 1e9;

            // Indexing runs in the background; wait until the collection is green and all vectors are indexed.
            CollectionInfo info;
            while (true) {
                info = client.getCollectionInfoAsync(COLLECTION).get();
                if (info.getStatus() == CollectionStatus.Green && info.getPointsCount() == n
                        && info.getIndexedVectorsCount() >= n) {
                    break;
                }
                Thread.sleep(1000);
            }
            double readySeconds = (System.nanoTime() - start) / 1e9;
            Map<String, Object> r = Results.record("qdrant-load");
            r.put("points", info.getPointsCount());
            r.put("indexed_vectors", info.getIndexedVectorsCount());
            r.put("segments", info.getSegmentsCount());
            r.put("m", m);
            r.put("ef_construct", efConstruct);
            r.put("upload_s", uploadSeconds);
            r.put("upload_and_index_s", readySeconds);
            Results.write(r);
        }
    }

    static void query(int ef, Data.Filter filter, String label) throws Exception {
        float[][] queries = Data.read(Data.QUERIES);
        int[][] truth = Data.truth(filter);
        try (QdrantClient client = connect()) {
            double[] ms = new double[queries.length];
            double recallSum = 0;
            int short_ = 0;
            for (int pass = 0; pass < 2; pass++) {
                for (int q = 0; q < queries.length; q++) {
                    QueryPoints.Builder request = QueryPoints.newBuilder()
                            .setCollectionName(COLLECTION)
                            .setQuery(nearest(queries[q]))
                            .setLimit(Data.K)
                            .setParams(SearchParams.newBuilder().setHnswEf(ef).build());
                    if (filter != Data.Filter.NONE) {
                        request.setFilter(Filter.newBuilder()
                                .addMust(range("category", Range.newBuilder().setLt(filter.bound).build())).build());
                    }
                    long start = System.nanoTime();
                    List<ScoredPoint> points = client.queryAsync(request.build()).get();
                    double elapsed = (System.nanoTime() - start) / 1e6;
                    if (pass == 1) {
                        ms[q] = elapsed;
                        long[] found = points.stream().mapToLong(p -> p.getId().getNum()).toArray();
                        recallSum += Results.recall(truth[q], found);
                        if (found.length < Data.K) {
                            short_++;
                        }
                    }
                }
            }
            Map<String, Object> r = Results.record("qdrant-query");
            r.put("label", label);
            r.put("filter", filter.label);
            r.put("hnsw_ef", ef);
            r.put("recall_at_10", recallSum / queries.length);
            r.put("fewer_than_10", short_);
            Results.latency(r, ms);
            Results.write(r);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
    }
}
