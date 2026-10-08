package demo.rag;

import java.util.List;

/** knowledge/manifest.json: the only documents that get into the index. */
public record Manifest(List<Entry> documents) {

    public record Entry(String id, String title, String topic, String objectClass, String source, String license,
                        String version, String retrievedAt, boolean core) {

        /** Path of the Markdown text below knowledge/. */
        public String path() {
            return topic + "/" + id + ".md";
        }
    }
}
