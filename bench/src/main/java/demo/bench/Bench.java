package demo.bench;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point. Run from the repository root with ./gradlew :bench:run --args="<command> [--option value]".
 *
 * <pre>
 * prepare      --parquet DIR                      Parquet -> data/base.f32, data/queries.f32
 * truth                                           exact top-10 per query and filter -> data/truth-*.i32
 * pg-load                                         COPY into items (vector) and items_half (halfvec)
 * pg-index     --type vector|halfvec [--m 16] [--ef-construction 64] [--workers 8] [--memory 12GB]
 * pg-btree     --create true|false                B-tree index on category
 * pg-query     --type vector|halfvec --ef 40,80 --filter none,1% [--iterative off,relaxed_order] [--label x]
 * qdrant-load  [--m 16] [--ef-construct 64]
 * qdrant-query --ef 40,80 --filter none,1% [--label x]
 * </pre>
 */
public final class Bench {

    private Bench() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("usage: see Bench.java");
            System.exit(2);
        }
        Map<String, String> o = options(Arrays.copyOfRange(args, 1, args.length));
        switch (args[0]) {
            case "prepare" -> Prepare.run(Path.of(required(o, "parquet").replaceFirst("^~",
                    System.getProperty("user.home"))));
            case "truth" -> Truth.run();
            case "pg-load" -> Pg.load();
            case "pg-index" -> Pg.index(type(o), integer(o, "m", 16), integer(o, "ef-construction", 64),
                    integer(o, "workers", 8), o.getOrDefault("memory", "12GB"));
            case "pg-btree" -> Pg.btree(Boolean.parseBoolean(o.getOrDefault("create", "true")));
            case "pg-query" -> {
                for (String iterative : list(o, "iterative", "off")) {
                    for (String filter : list(o, "filter", "none")) {
                        for (String ef : list(o, "ef", "40")) {
                            Pg.query(type(o), Integer.parseInt(ef), Data.Filter.parse(filter), iterative,
                                    o.getOrDefault("label", ""));
                        }
                    }
                }
            }
            case "qdrant-load" -> QdrantBench.load(integer(o, "m", 16), integer(o, "ef-construct", 64));
            case "qdrant-query" -> {
                for (String filter : list(o, "filter", "none")) {
                    for (String ef : list(o, "ef", "40")) {
                        QdrantBench.query(Integer.parseInt(ef), Data.Filter.parse(filter),
                                o.getOrDefault("label", ""));
                    }
                }
            }
            default -> throw new IllegalArgumentException("unknown command " + args[0]);
        }
    }

    private static Map<String, String> options(String[] args) {
        Map<String, String> o = new HashMap<>();
        for (int i = 0; i + 1 < args.length; i += 2) {
            if (!args[i].startsWith("--")) {
                throw new IllegalArgumentException("expected --option, got " + args[i]);
            }
            o.put(args[i].substring(2), args[i + 1]);
        }
        return o;
    }

    private static String required(Map<String, String> o, String key) {
        String v = o.get(key);
        if (v == null) {
            throw new IllegalArgumentException("--" + key + " is required");
        }
        return v;
    }

    private static int integer(Map<String, String> o, String key, int fallback) {
        return o.containsKey(key) ? Integer.parseInt(o.get(key)) : fallback;
    }

    private static List<String> list(Map<String, String> o, String key, String fallback) {
        return Arrays.asList(o.getOrDefault(key, fallback).split(","));
    }

    private static Pg.Type type(Map<String, String> o) {
        return Pg.Type.valueOf(required(o, "type").toUpperCase());
    }
}
