-- AI Smart Demo PostgreSQL 初始化脚本
-- 需要 pgvector 扩展

CREATE EXTENSION IF NOT EXISTS vector;

-- 知识分片表（向量存储）
CREATE TABLE IF NOT EXISTS t_knowledge_chunk (
    id BIGINT NOT NULL PRIMARY KEY,
    doc_id BIGINT NOT NULL,
    chunk_index INT NOT NULL,
    content TEXT,
    embedding vector(1536),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建向量索引
CREATE INDEX IF NOT EXISTS idx_chunk_embedding ON t_knowledge_chunk USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
CREATE INDEX IF NOT EXISTS idx_chunk_doc_id ON t_knowledge_chunk (doc_id);
