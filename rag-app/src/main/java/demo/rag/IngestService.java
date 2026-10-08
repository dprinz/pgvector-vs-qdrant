package demo.rag;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;

/** Reads, chunks and embeds every document of the manifest whose version is not yet in the index. */
@ApplicationScoped
public class IngestService {

    public record Result(int indexed, int skipped, int chunks) {
    }

    private final Corpus corpus;
    private final Chunker chunker;
    private final Embedder embedder;
    private final KnowledgeRepository repository;
    private final boolean ingestOnStart;

    public IngestService(Corpus corpus, Chunker chunker, Embedder embedder, KnowledgeRepository repository,
                         @ConfigProperty(name = "rag.ingest-on-start") boolean ingestOnStart) {
        this.corpus = corpus;
        this.chunker = chunker;
        this.embedder = embedder;
        this.repository = repository;
        this.ingestOnStart = ingestOnStart;
    }

    void onStart(@Observes StartupEvent event) {
        if (ingestOnStart) {
            Result result = ingest();
            Log.infof("Knowledge base: %d documents indexed, %d unchanged, %d chunks written",
                    result.indexed(), result.skipped(), result.chunks());
        }
    }

    public Result ingest() {
        Map<String, String> indexed = repository.versions();
        int done = 0;
        int skipped = 0;
        int chunks = 0;
        for (Manifest.Entry entry : corpus.manifest().documents()) {
            if (entry.version().equals(indexed.get(entry.id()))) {
                skipped++;
                continue;
            }
            List<Chunker.Chunk> parts = chunker.chunk(corpus.text(entry));
            List<float[]> embeddings = embedder.passages(parts.stream().map(Chunker.Chunk::text).toList());
            repository.replace(entry, parts, embeddings);
            done++;
            chunks += parts.size();
        }
        return new Result(done, skipped, chunks);
    }
}
