# pgvector vs. Qdrant

Companion repository for a blog article on [systemebene.house-harkonnen.com](https://systemebene.house-harkonnen.com).
Two parts:

| Part | What it shows |
| --- | --- |
| [`rag-app/`](rag-app) | Retrieval for a RAG app with Quarkus 3.40 LTS, LangChain4j and pgvector: a small astronomy corpus, an in-process ONNX embedding model, ingest in one transaction per document, vector and hybrid search in one SQL statement, and an evaluation with 23 German questions. |
| [`bench/`](bench) | One million OpenAI embeddings (1536 dimensions) in pgvector and Qdrant: HNSW build time and size, `vector` against `halfvec`, recall against latency, filtered search with and without iterative index scans. |

Versions: Java 25, Quarkus 3.40.1, PostgreSQL 18.6 with pgvector 0.8.7, Qdrant 1.19.1, Gradle 9.8.1.

## Tutorial app

Requires Docker (Quarkus Dev Services starts PostgreSQL with pgvector) and JDK 25.

```bash
./gradlew :rag-app:quarkusDev
```

The first build downloads the embedding model
([`Xenova/multilingual-e5-small`](https://huggingface.co/Xenova/multilingual-e5-small), quantized ONNX, 118 MB,
pinned revision and SHA-256). At start the app indexes the corpus in [`knowledge/`](knowledge) (48 documents, 643
chunks). Then:

```bash
curl -G localhost:8080/search --data-urlencode "q=Was ist ein H-Alpha-Filter?"
curl -G localhost:8080/search --data-urlencode "q=Wofür brauche ich einen OIII-Filter?" -d topic=filters -d mode=hybrid
```

Files worth reading:

- [`V1__knowledge.sql`](rag-app/src/main/resources/db/migration/V1__knowledge.sql): schema, `vector(384)`, generated `tsvector`
- [`KnowledgeRepository.java`](rag-app/src/main/java/demo/rag/KnowledgeRepository.java): ingest transaction, vector search, hybrid search with Reciprocal Rank Fusion
- [`Embedder.java`](rag-app/src/main/java/demo/rag/Embedder.java): ONNX model in the JVM, e5 prefixes
- [`Chunker.java`](rag-app/src/main/java/demo/rag/Chunker.java): sections along headings, 250 words, 40 overlap

Evaluation (hit rate, MRR, latency, vector binding, HNSW against exact search):

```bash
./gradlew :rag-app:evaluate     # writes rag-app/build/reports/evaluation.md
```

## Benchmark

Dataset: [`Qdrant/dbpedia-entities-openai3-text-embedding-3-large-1536-1M`](https://huggingface.co/datasets/Qdrant/dbpedia-entities-openai3-text-embedding-3-large-1536-1M)
(MIT, about 9.6 GB of Parquet). It is published by Qdrant and widely used for vector database benchmarks. Every
1000th row is held out as a query (1,000 queries, 999,000 indexed vectors). The exact top 10 per query is computed by
brute force in Java, independent of both databases. The dataset has no metadata, so each vector gets a synthetic
`category` (0..999, a hash of the id) for the filter tests: 10 %, 1 % and 0.1 % of the rows match.

Both databases run in Docker on the same machine, one at a time, with the same HNSW parameters (m = 16,
ef_construction = 64) and the same distance (inner product; the OpenAI vectors are normalised, so this equals cosine).
Latencies are measured in the client, single-threaded, after one warm-up pass.

```bash
./gradlew :bench:run --args="prepare --parquet /path/to/dbpedia-parquet"
./gradlew :bench:run --args="truth"

docker compose up -d postgres
./gradlew :bench:run --args="pg-load"
./gradlew :bench:run --args="pg-index --type vector"
./gradlew :bench:run --args="pg-query --type vector --ef 40,80,160,320 --filter none"
docker compose stop postgres

docker compose up -d qdrant
./gradlew :bench:run --args="qdrant-load"
./gradlew :bench:run --args="qdrant-query --ef 40,80,160,320 --filter none"
```

Results are appended to [`bench/results/results.jsonl`](bench/results). Docker Desktop needs about 24 GB of memory
for the pgvector index build (see `maintenance_work_mem` and `shm_size` in [`compose.yml`](compose.yml)).

## License

Code: MIT. Corpus: CC BY-SA 4.0, see [`knowledge/README.md`](knowledge/README.md).
