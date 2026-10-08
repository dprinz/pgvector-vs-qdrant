package demo.rag;

/**
 * One search result. score is the cosine distance for the vector search (0 = same direction, 2 = opposite) and the
 * RRF score for the hybrid search (higher is better).
 */
public record Hit(String documentId, String title, String section, String text, double score) {
}
