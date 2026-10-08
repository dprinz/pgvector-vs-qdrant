package demo.bench;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** Appends one JSON object per measurement to results/results.jsonl and prints it. */
final class Results {

    static final Path FILE = Path.of("results/results.jsonl");

    private Results() {
    }

    static Map<String, Object> record(String kind) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("at", Instant.now().toString());
        r.put("kind", kind);
        return r;
    }

    static void write(Map<String, Object> r) throws IOException {
        String json = r.entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\":" + value(e.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
        Files.createDirectories(FILE.getParent());
        Files.writeString(FILE, json + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println(json);
    }

    private static String value(Object v) {
        if (v == null) {
            return "null";
        }
        if (v instanceof Double d) {
            return String.format(Locale.ROOT, "%.4f", d);
        }
        if (v instanceof Number || v instanceof Boolean) {
            return v.toString();
        }
        return "\"" + v.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    /** Latencies in ms: p50, p95, p99, mean. */
    static void latency(Map<String, Object> r, double[] ms) {
        double[] sorted = ms.clone();
        Arrays.sort(sorted);
        r.put("p50_ms", pct(sorted, 50));
        r.put("p95_ms", pct(sorted, 95));
        r.put("p99_ms", pct(sorted, 99));
        r.put("mean_ms", Arrays.stream(ms).average().orElse(Double.NaN));
    }

    private static double pct(double[] sorted, int p) {
        int index = (int) Math.ceil(p / 100.0 * sorted.length) - 1;
        return sorted[Math.max(0, index)];
    }

    /** Share of the exact top-K ids that the search returned. */
    static double recall(int[] exact, long[] found) {
        int hits = 0;
        for (int id : exact) {
            for (long f : found) {
                if (f == id) {
                    hits++;
                    break;
                }
            }
        }
        return exact.length == 0 ? 1.0 : (double) hits / exact.length;
    }
}
