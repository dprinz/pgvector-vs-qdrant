package demo.rag;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Splits a Markdown document along its headings. Each section becomes one or more chunks of at most maxWords words;
 * consecutive chunks of a section overlap by overlapWords. The first line of every chunk names document and section,
 * so a chunk still says what it is about when it is read on its own.
 */
@ApplicationScoped
public class Chunker {

    public static final String INTRODUCTION = "Introduction";

    private final int maxWords;
    private final int overlapWords;

    public Chunker(@ConfigProperty(name = "rag.chunk.max-words") int maxWords,
                   @ConfigProperty(name = "rag.chunk.overlap-words") int overlapWords) {
        if (overlapWords >= maxWords) {
            throw new IllegalArgumentException("overlap must be smaller than a chunk");
        }
        this.maxWords = maxWords;
        this.overlapWords = overlapWords;
    }

    public record Chunk(int position, String section, String text) {
    }

    public List<Chunk> chunk(String markdown) {
        String title = "";
        String section = INTRODUCTION;
        List<String> lines = new ArrayList<>();
        List<Chunk> chunks = new ArrayList<>();
        for (String raw : markdown.split("\n")) {
            String line = raw.strip();
            if (line.startsWith("# ")) {
                title = line.substring(2).strip();
            } else if (line.matches("#{2,6} .*")) {
                cut(chunks, title, section, lines);
                section = line.replaceFirst("#{2,6} ", "").strip();
            } else if (!line.isEmpty()) {
                lines.add(line.startsWith("- ") ? line.substring(2) : line);
            }
        }
        cut(chunks, title, section, lines);
        return chunks;
    }

    private void cut(List<Chunk> chunks, String title, String section, List<String> lines) {
        List<String> words = new ArrayList<>();
        lines.forEach(line -> words.addAll(Arrays.asList(line.split("\\s+"))));
        lines.clear();
        int start = 0;
        while (start < words.size()) {
            int end = Math.min(words.size(), start + maxWords);
            String text = title + " – " + section + "\n" + String.join(" ", words.subList(start, end));
            chunks.add(new Chunk(chunks.size(), section, text));
            if (end == words.size()) {
                break;
            }
            start = end - overlapWords;
        }
    }
}
