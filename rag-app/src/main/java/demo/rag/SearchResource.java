package demo.rag;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Set;

/** GET /search?q=Was ist ein H-Alpha-Filter?&topic=filters&k=5&mode=vector */
@Path("/search")
public class SearchResource {

    private static final Set<String> TOPICS = Set.of("filters", "imaging", "equipment", "targets");

    private final Embedder embedder;
    private final KnowledgeRepository repository;

    public SearchResource(Embedder embedder, KnowledgeRepository repository) {
        this.embedder = embedder;
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Hit> search(@QueryParam("q") String q, @QueryParam("topic") String topic,
                            @QueryParam("k") @DefaultValue("5") int k,
                            @QueryParam("mode") @DefaultValue("vector") String mode) {
        if (q == null || q.isBlank() || q.length() > 500) {
            throw new BadRequestException("q: 1 to 500 characters");
        }
        if (topic != null && !TOPICS.contains(topic)) {
            throw new BadRequestException("topic: one of " + TOPICS);
        }
        if (k < 1 || k > 20) {
            throw new BadRequestException("k: 1 to 20");
        }
        KnowledgeRepository.Mode searchMode = switch (mode) {
            case "vector" -> KnowledgeRepository.Mode.VECTOR;
            case "hybrid" -> KnowledgeRepository.Mode.HYBRID;
            default -> throw new BadRequestException("mode: vector or hybrid");
        };
        return repository.search(embedder.query(q), q, topic, k, searchMode);
    }
}
