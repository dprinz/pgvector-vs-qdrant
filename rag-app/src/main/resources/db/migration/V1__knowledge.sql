-- Knowledge base of the RAG app: documents from knowledge/manifest.json and their chunks with embeddings of
-- multilingual-e5-small (384 dimensions).

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE document (
    id      text PRIMARY KEY,
    title   text NOT NULL,
    topic   text NOT NULL,
    source  text NOT NULL,
    version text NOT NULL
);

CREATE TABLE chunk (
    document_id text        NOT NULL REFERENCES document (id) ON DELETE CASCADE,
    position    integer     NOT NULL,
    section     text        NOT NULL,
    text        text        NOT NULL,
    embedding   vector(384) NOT NULL,
    -- Full text for the hybrid search. 'simple' does no stemming and keeps stop words: the corpus is English, the
    -- questions are German, so no language configuration fits both.
    tsv         tsvector GENERATED ALWAYS AS (to_tsvector('simple', text)) STORED,
    PRIMARY KEY (document_id, position)
);

CREATE INDEX chunk_tsv_idx ON chunk USING gin (tsv);

-- No HNSW index: with a few hundred chunks the exact search takes about a millisecond and is always complete.
-- For a larger corpus:
--   CREATE INDEX chunk_embedding_idx ON chunk USING hnsw (embedding vector_cosine_ops);
