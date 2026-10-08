package demo.bench;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Exact top-K by inner product for every query and filter, by brute force over all base vectors. Computed in Java so
 * that the reference does not come from either database.
 */
final class Truth {

    private Truth() {
    }

    /** Top-K with the smallest kept score at index 0 of a sorted array; good enough for K = 10. */
    static final class TopK {
        final int[] ids = new int[Data.K];
        final float[] scores = new float[Data.K];
        int size;

        TopK() {
            java.util.Arrays.fill(scores, Float.NEGATIVE_INFINITY);
        }

        void offer(int id, float score) {
            if (size == Data.K && score <= scores[0]) {
                return;
            }
            int i = size == Data.K ? 0 : size++;
            // replace the minimum (or append), then bubble up to keep ascending order
            ids[i] = id;
            scores[i] = score;
            while (i + 1 < size && scores[i] > scores[i + 1]) {
                swap(i, i + 1);
                i++;
            }
            while (i > 0 && scores[i] < scores[i - 1]) {
                swap(i, i - 1);
                i--;
            }
        }

        void merge(TopK other) {
            for (int i = 0; i < other.size; i++) {
                offer(other.ids[i], other.scores[i]);
            }
        }

        /** Ids from best to worst. */
        int[] best() {
            int[] out = new int[size];
            for (int i = 0; i < size; i++) {
                out[i] = ids[size - 1 - i];
            }
            return out;
        }

        private void swap(int a, int b) {
            int id = ids[a];
            ids[a] = ids[b];
            ids[b] = id;
            float s = scores[a];
            scores[a] = scores[b];
            scores[b] = s;
        }
    }

    static void run() throws Exception {
        long start = System.nanoTime();
        float[][] base = Data.read(Data.BASE);
        float[][] queries = Data.read(Data.QUERIES);
        System.out.printf("loaded %,d base vectors and %,d queries in %.0f s%n", base.length, queries.length,
                (System.nanoTime() - start) / 1e9);
        Data.Filter[] filters = Data.Filter.values();
        int[] categories = new int[base.length];
        for (int i = 0; i < base.length; i++) {
            categories[i] = Data.category(i);
        }

        int threads = Runtime.getRuntime().availableProcessors();
        int slice = (base.length + threads - 1) / threads;
        List<Future<TopK[][]>> parts = new ArrayList<>();
        start = System.nanoTime();
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                int from = t * slice;
                int to = Math.min(base.length, from + slice);
                parts.add(pool.submit(() -> {
                    TopK[][] top = new TopK[queries.length][filters.length];
                    for (TopK[] row : top) {
                        for (int f = 0; f < filters.length; f++) {
                            row[f] = new TopK();
                        }
                    }
                    for (int id = from; id < to; id++) {
                        float[] b = base[id];
                        int category = categories[id];
                        for (int q = 0; q < queries.length; q++) {
                            float score = dot(b, queries[q]);
                            for (int f = 0; f < filters.length; f++) {
                                if (filters[f].matches(category)) {
                                    top[q][f].offer(id, score);
                                }
                            }
                        }
                    }
                    return top;
                }));
            }
            TopK[][] all = parts.getFirst().get();
            for (int p = 1; p < parts.size(); p++) {
                TopK[][] part = parts.get(p).get();
                for (int q = 0; q < queries.length; q++) {
                    for (int f = 0; f < filters.length; f++) {
                        all[q][f].merge(part[q][f]);
                    }
                }
            }
            for (int f = 0; f < filters.length; f++) {
                try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                        Files.newOutputStream(filters[f].truth())))) {
                    out.writeInt(queries.length);
                    out.writeInt(Data.K);
                    for (int q = 0; q < queries.length; q++) {
                        for (int id : all[q][f].best()) {
                            out.writeInt(id);
                        }
                    }
                }
            }
        }
        long matching = 0;
        for (int c : categories) {
            if (Data.Filter.ONE.matches(c)) {
                matching++;
            }
        }
        System.out.printf("truth for %d filters in %.0f s (filter 1%% matches %,d base vectors)%n", filters.length,
                (System.nanoTime() - start) / 1e9, matching);
    }

    static float dot(float[] a, float[] b) {
        float s0 = 0;
        float s1 = 0;
        float s2 = 0;
        float s3 = 0;
        for (int i = 0; i < a.length; i += 4) {
            s0 += a[i] * b[i];
            s1 += a[i + 1] * b[i + 1];
            s2 += a[i + 2] * b[i + 2];
            s3 += a[i + 3] * b[i + 3];
        }
        return (s0 + s1) + (s2 + s3);
    }
}
