package com.smart.application.service;

import com.smart.common.exception.BizException;
import com.smart.common.exception.ErrorCode;
import com.smart.domain.entity.KnowledgeDoc;
import com.smart.domain.mapper.KnowledgeDocMapper;
import com.smart.infrastructure.rag.RagPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识库应用服务 —— 管理知识文档的上传、检索和生命周期
 *
 * <p>在 DDD 分层架构中，本类属于 <b>Application 层（应用服务层）</b>，
 * 负责编排知识文档的业务用例，是 RAG（检索增强生成）流程的数据入口。</p>
 *
 * <h3>核心职责</h3>
 * <ul>
 *   <li><b>文档上传与 RAG 入库</b>：将知识文档持久化到数据库后，调用 RAG 管道服务进行文本分块（Chunking）、
 *       向量化（Embedding）、写入向量数据库，使其可被 AI 决策流程中的 RAG 检索阶段召回</li>
 *   <li><b>知识检索</b>：封装 RAG 管道的语义检索能力，支持根据自然语言查询召回最相关的知识片段</li>
 *   <li><b>文档 CRUD</b>：提供基础的文档查询和删除操作</li>
 * </ul>
 *
 * <h3>与决策流程的关系</h3>
 * <p>本服务上传的知识文档最终会被 {@link DecisionAppService} 在第二阶段（RAG 检索）中使用，
 * 为 AI 大模型推理提供领域知识上下文，提升决策质量。</p>
 *
 * @author Joseph Ho
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeAppService {

    /** 知识文档 Mapper，负责文档元数据的数据库持久化 */
    private final KnowledgeDocMapper knowledgeDocMapper;

    /** RAG 管道服务，负责文档分块、向量化入库以及语义检索 */
    private final RagPipelineService ragPipelineService;

    /**
     * 上传知识文档并触发 RAG 入库流程
     *
     * <p>处理流程：</p>
     * <ol>
     *   <li>将文档元数据（文件名、路径等）插入数据库，获取文档 ID</li>
     *   <li>调用 RAG 管道服务对文档进行处理：读取文件 → 文本分块（Chunking） → 向量化（Embedding） → 写入向量数据库</li>
     *   <li>将分块数量（chunkCount）回写到文档记录中，便于后续统计和管理</li>
     * </ol>
     *
     * <p><b>注意：</b>当前 RAG 入库是同步执行的，对于大文档可能耗时较长，
     * 后续可优化为异步处理（如通过消息队列解耦）。</p>
     *
     * @param doc 知识文档实体，包含文件名、文件路径等元数据
     * @return 上传成功的文档实体（含数据库 ID 和分块数量）
     */
    public KnowledgeDoc upload(KnowledgeDoc doc) {
        // 第一步：持久化文档元数据到数据库
        knowledgeDocMapper.insert(doc);
        log.info("上传知识文档: {}", doc.getId());
        // 第二步：调用 RAG 管道进行文档分块和向量化入库，返回分块数量
        int chunkCount = ragPipelineService.ingest(doc.getId(), doc.getFilePath());
        // 第三步：将分块数量回写到文档记录
        doc.setChunkCount(chunkCount);
        knowledgeDocMapper.updateById(doc);
        return doc;
    }

    /**
     * 根据 ID 查询知识文档
     *
     * @param id 文档ID
     * @return 知识文档实体
     * @throws BizException 当文档不存在时抛出 DATA_NOT_FOUND 业务异常
     */
    public KnowledgeDoc getById(Long id) {
        KnowledgeDoc doc = knowledgeDocMapper.selectById(id);
        if (doc == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return doc;
    }

    /**
     * 查询所有知识文档
     *
     * @return 知识文档列表
     */
    public List<KnowledgeDoc> listAll() {
        return knowledgeDocMapper.selectList(null);
    }

    /**
     * RAG 语义检索 —— 根据自然语言查询召回最相关的知识片段
     *
     * <p>底层调用向量数据库的相似度检索（如余弦相似度），
     * 返回与查询语义最接近的 Top-K 个文本片段。</p>
     *
     * @param query 自然语言查询字符串
     * @param topK  返回的最大片段数量
     * @return 最相关的知识文本片段列表
     */
    public List<String> retrieve(String query, int topK) {
        return ragPipelineService.retrieve(query, topK);
    }

    /**
     * 根据 ID 删除知识文档
     *
     * <p><b>注意：</b>当前仅删除数据库中的文档记录，未同步清理向量数据库中的对应向量数据，
     * 后续应补充向量数据的级联删除逻辑。</p>
     *
     * @param id 文档ID
     */
    public void delete(Long id) {
        knowledgeDocMapper.deleteById(id);
        log.info("删除知识文档: {}", id);
    }
}
