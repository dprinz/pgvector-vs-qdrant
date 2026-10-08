package demo.bench;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

/**
 * Converts the Parquet files of Qdrant/dbpedia-entities-openai3-text-embedding-3-large-1536-1M into two flat float32
 * files. Every 1000th row becomes a query and is not indexed; the remaining 999,000 rows are the base.
 */
final class Prepare {

    private Prepare() {
    }

    static void run(Path parquetDir) throws Exception {
        List<Path> files;
        try (Stream<Path> list = Files.list(parquetDir)) {
            files = list.filter(p -> p.toString().endsWith(".parquet")).sorted().toList();
        }
        if (files.isEmpty()) {
            throw new IllegalArgumentException("no .parquet files in " + parquetDir);
        }
        Files.createDirectories(Data.DIR);
        ByteBuffer buffer = ByteBuffer.allocate(4 * Data.DIM).order(ByteOrder.LITTLE_ENDIAN);
        long row = 0;
        int base = 0;
        int queries = 0;
        double minNorm = Double.MAX_VALUE;
        double maxNorm = 0;
        long start = System.nanoTime();
        try (Connection duck = DriverManager.getConnection("jdbc:duckdb:");
             OutputStream baseOut = new BufferedOutputStream(Files.newOutputStream(Data.BASE), 1 << 22);
             OutputStream queryOut = new BufferedOutputStream(Files.newOutputStream(Data.QUERIES), 1 << 20)) {
            for (Path file : files) {
                // A single-file scan keeps the row order of the file.
                String sql = "SELECT \"text-embedding-3-large-1536-embedding\" FROM read_parquet('"
                        + file.toAbsolutePath().toString().replace("'", "''") + "')";
                try (Statement st = duck.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                    while (rs.next()) {
                        Array array = rs.getArray(1);
                        Object[] values = (Object[]) array.getArray();
                        if (values.length != Data.DIM) {
                            throw new IllegalStateException("row " + row + " has " + values.length + " dimensions");
                        }
                        buffer.clear();
                        double norm = 0;
                        for (Object value : values) {
                            float f = ((Number) value).floatValue();
                            norm += (double) f * f;
                            buffer.putFloat(f);
                        }
                        norm = Math.sqrt(norm);
                        minNorm = Math.min(minNorm, norm);
                        maxNorm = Math.max(maxNorm, norm);
                        if (row % 1000 == 999) {
                            queryOut.write(buffer.array());
                            queries++;
                        } else {
                            baseOut.write(buffer.array());
                            base++;
                        }
                        row++;
                    }
                }
                System.out.printf("%s: %,d rows so far%n", file.getFileName(), row);
            }
        }
        System.out.printf("base %,d, queries %,d, norm %.6f..%.6f, %.0f s%n", base, queries, minNorm, maxNorm,
                (System.nanoTime() - start) / 1e9);
    }
}
