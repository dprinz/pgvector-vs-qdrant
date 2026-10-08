package demo.rag;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class SearchTest {

    @Inject
    IngestService ingest;
    @Inject
    Embedder embedder;
    @Inject
    KnowledgeRepository repository;

    @BeforeEach
    void index() {
        ingest.ingest();
    }

    @Test
    void findsTheFilterSectionForAGermanQuestion() {
        String question = "Was ist ein H-Alpha-Filter?";
        List<Hit> hits = repository.search(embedder.query(question), question, null, 5,
                KnowledgeRepository.Mode.VECTOR);
        assertEquals(5, hits.size());
        assertTrue(hits.stream().anyMatch(h -> h.documentId().equals("hydrogen-alpha") && h.section().equals("Filter")),
                hits.toString());
    }

    @Test
    void topicFilterOnlyReturnsThatTopic() {
        String question = "Was ist ein H-Alpha-Filter?";
        for (KnowledgeRepository.Mode mode : KnowledgeRepository.Mode.values()) {
            List<Hit> hits = repository.search(embedder.query(question), question, "equipment", 5, mode);
            assertEquals(5, hits.size(), mode.name());
            assertTrue(hits.stream().noneMatch(h -> h.documentId().equals("hydrogen-alpha")), mode.name());
        }
    }

    @Test
    void secondIngestSkipsUnchangedDocuments() {
        IngestService.Result again = ingest.ingest();
        assertEquals(0, again.indexed());
        assertTrue(again.skipped() > 40);
    }

    @Test
    void orQuery() {
        assertEquals("was or ist or ein or h or alpha or filter",
                KnowledgeRepository.orQuery("Was ist ein H-Alpha-Filter?"));
    }
}
