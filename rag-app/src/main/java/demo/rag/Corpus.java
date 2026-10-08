package demo.rag;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/** Reads the curated corpus from the classpath (copied from knowledge/ at build time). */
@ApplicationScoped
public class Corpus {

    private final ObjectMapper mapper;

    public Corpus(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Manifest manifest() {
        try (InputStream in = open("knowledge/manifest.json")) {
            return mapper.readValue(in, Manifest.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String text(Manifest.Entry entry) {
        try (InputStream in = open("knowledge/" + entry.path())) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static InputStream open(String resource) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("missing resource " + resource);
        }
        return in;
    }
}
