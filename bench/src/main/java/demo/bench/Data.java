package demo.bench;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.IntPredicate;

/**
 * Prepared data below bench/data:
 * <ul>
 *   <li>base.f32 – the indexed vectors, little-endian float32, row i has id i</li>
 *   <li>queries.f32 – the held-out query vectors (every 1000th row of the dataset)</li>
 *   <li>truth-&lt;filter&gt;.i32 – exact top-K ids per query, by inner product, computed in Java</li>
 * </ul>
 */
final class Data {

    static final Path DIR = Path.of("data");
    static final Path BASE = DIR.resolve("base.f32");
    static final Path QUERIES = DIR.resolve("queries.f32");
    static final int DIM = 1536;
    static final int K = 10;

    /** Filters on the synthetic payload field category (0..999, uniform). */
    enum Filter {
        NONE("none", 1000), TEN("10%", 100), ONE("1%", 10), TENTH("0.1%", 1);

        final String label;
        /** Matches category &lt; bound. */
        final int bound;

        Filter(String label, int bound) {
            this.label = label;
            this.bound = bound;
        }

        boolean matches(int category) {
            return category < bound;
        }

        Path truth() {
            return DIR.resolve("truth-" + name().toLowerCase() + ".i32");
        }

        static Filter parse(String text) {
            for (Filter f : values()) {
                if (f.label.equals(text) || f.name().equalsIgnoreCase(text)) {
                    return f;
                }
            }
            throw new IllegalArgumentException("filter: none, 10%, 1% or 0.1%");
        }
    }

    private Data() {
    }

    /**
     * Synthetic category of a base vector: a hash of the id, so that it does not correlate with the order of the
     * dataset nor with the content. The dataset itself has no metadata to filter on.
     */
    static int category(int id) {
        long z = id * 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        z ^= z >>> 31;
        return (int) Math.floorMod(z, 1000L);
    }

    static int count(Path vectors) throws IOException {
        return (int) (Files.size(vectors) / (4L * DIM));
    }

    static float[][] read(Path vectors) throws IOException {
        int n = count(vectors);
        float[][] all = new float[n][];
        try (VectorReader reader = new VectorReader(vectors)) {
            for (int i = 0; i < n; i++) {
                all[i] = reader.next();
            }
        }
        return all;
    }

    static int[][] truth(Filter filter) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(filter.truth())))) {
            int queries = in.readInt();
            int k = in.readInt();
            int[][] ids = new int[queries][k];
            for (int q = 0; q < queries; q++) {
                for (int j = 0; j < k; j++) {
                    ids[q][j] = in.readInt();
                }
            }
            return ids;
        }
    }

    static IntPredicate predicate(Filter filter) {
        return id -> filter.matches(category(id));
    }

    /** Streams vectors from a .f32 file. */
    static final class VectorReader implements AutoCloseable {
        private final InputStream in;
        private final byte[] bytes = new byte[4 * DIM];
        private final ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        VectorReader(Path path) throws IOException {
            this.in = new BufferedInputStream(Files.newInputStream(path), 1 << 22);
        }

        float[] next() throws IOException {
            in.readNBytes(bytes, 0, bytes.length);
            float[] v = new float[DIM];
            buffer.rewind();
            buffer.asFloatBuffer().get(v);
            return v;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}
