package demo.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Path;
import java.util.List;

/**
 * multilingual-e5-small in-process via ONNX: no embedding server, the question never leaves the JVM.
 * e5 was trained with the prefixes "query: " and "passage: "; without them the ranking gets noticeably worse.
 */
@ApplicationScoped
public class Embedder {

    public static final int DIMENSIONS = 384;

    private final EmbeddingModel model;

    public Embedder(@ConfigProperty(name = "rag.model-dir") Path modelDir) {
        this.model = new OnnxEmbeddingModel(modelDir.resolve("model_quantized.onnx"),
                modelDir.resolve("tokenizer.json"), PoolingMode.MEAN);
    }

    public float[] query(String text) {
        return model.embed("query: " + text).content().vector();
    }

    public List<float[]> passages(List<String> texts) {
        List<TextSegment> segments = texts.stream().map(text -> TextSegment.from("passage: " + text)).toList();
        return model.embedAll(segments).content().stream().map(Embedding::vector).toList();
    }
}
