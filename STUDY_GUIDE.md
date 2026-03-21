# AI 智能决策系统 — 学习指南

> 本文档是 `ai-smart-demo` 项目的学习指南，帮助你快速理解项目架构、核心技术栈、业务流程和代码设计思路。
> 适合面试准备、代码走读和后续开发参考。

---

## 目录

1. [项目概述](#1-项目概述)
2. [技术栈全景](#2-技术栈全景)
3. [Maven 多模块架构](#3-maven-多模块架构)
4. [DDD 分层设计详解](#4-ddd-分层设计详解)
5. [核心业务流程](#5-核心业务流程)
6. [RAG 知识检索管道](#6-rag-知识检索管道)
7. [双数据源配置详解](#7-双数据源配置详解)
8. [Redis 缓存设计](#8-redis-缓存设计)
9. [Kafka 消息驱动](#9-kafka-消息驱动)
10. [规则引擎设计](#10-规则引擎设计)
11. [全链路日志追踪](#11-全链路日志追踪)
12. [代码规范与质量](#12-代码规范与质量)
13. [面试高频问题与回答要点](#13-面试高频问题与回答要点)
14. [扩展阅读](#14-扩展阅读)

---

## 1. 项目概述

### 1.1 业务背景

本项目是一个**光伏储能领域的 AI 智能决策系统**，核心目标是将传统的"人写规则"升级为"AI 辅助决策"。

**应用场景**：
- IoT 设备（逆变器、电池、电表等）实时上报工作数据
- 系统通过 Kafka 消费数据，匹配电站绑定的智能化场景
- 先走**规则引擎**判断触发条件
- 命中规则后走 **RAG 检索**知识库（设备手册、故障案例、运维规范）
- 由 **LLM** 生成智能决策建议
- 系统保留**规则兜底机制**，AI 置信度低时回退到规则执行

### 1.2 系统架构总览

```
┌──────────────────────────────────────────────────────────────┐
│                       IoT 设备层                              │
│         逆变器 / 储能电池 / 电表 / 热泵 / 充电桩              │
└─────────────────────┬────────────────────────────────────────┘
                      │ 设备数据上报
                      ▼
┌──────────────────────────────────────────────────────────────┐
│                    Kafka (device-data)                        │
│                    消息队列 - 解耦 & 削峰                      │
└─────────────────────┬────────────────────────────────────────┘
                      │ DeviceDataConsumer 消费
                      ▼
┌──────────────────────────────────────────────────────────────┐
│              DecisionAppService（决策编排）                    │
│                                                              │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐ │
│  │ 阶段1     │   │ 阶段2     │   │ 阶段3     │   │ 阶段4     │ │
│  │ 规则匹配  │──▶│ RAG检索   │──▶│ AI推理    │──▶│ 指令下发  │ │
│  │ (Redis    │   │ (pgvector │   │ (Spring   │   │ (Kafka   │ │
│  │  缓存)    │   │  向量库)  │   │  AI/LLM)  │   │  发布)   │ │
│  └──────────┘   └──────────┘   └──────────┘   └──────────┘ │
│                                                              │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              全链路日志 → Elasticsearch                   │ │
│  └─────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
      ┌────────┐ ┌────────┐ ┌────────┐
      │ MySQL  │ │ Redis  │ │ Kafka  │
      │ 持久化 │ │ 缓存   │ │ 结果   │
      │ 决策   │ │ 决策   │ │ 发布   │
      └────────┘ └────────┘ └────────┘
```

### 1.3 存储职责分工

| 存储 | 用途 | 选型理由 |
|------|------|----------|
| **MySQL** | 业务主库（电站、设备、场景、规则、决策记录、知识文档元数据） | 事务支持、关系查询 |
| **PostgreSQL + pgvector** | 文档分块向量存储 | 原生向量索引、高效余弦相似度检索 |
| **Redis** | 场景配置缓存、决策结果缓存 | 低延迟读取、减轻数据库压力 |
| **Elasticsearch** | 决策执行全链路日志 | 全文检索、按时间范围聚合分析 |
| **Kafka** | 设备数据接入、决策结果分发 | 解耦生产消费、削峰填谷、支持重放 |

---

## 2. 技术栈全景

### 2.1 核心框架

| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17 | 语言版本，支持 Records、Sealed Classes、Text Blocks |
| **Spring Boot** | 3.2.5 | 应用框架，自动配置、嵌入式 Tomcat |
| **Spring AI** | 1.0.0-M4 | LLM 集成（OpenAI 兼容接口）、Embedding、RAG |
| **MyBatis Plus** | 3.5.5 | ORM 框架，简化 CRUD，雪花算法 ID |
| **SpringDoc** | 2.3.0 | OpenAPI 3.0 文档，替代 SpringFox |

### 2.2 中间件

| 中间件 | 版本 | 本项目中的作用 |
|--------|------|----------------|
| **MySQL** | 8.x | 业务数据持久化 |
| **PostgreSQL** | 16 + pgvector | 向量数据库，存储文档 embedding |
| **Redis** | 7.x | 缓存（场景配置、决策结果） |
| **Kafka** | Confluent | 消息队列（设备数据消费、决策结果发布） |
| **Elasticsearch** | 8.x | 日志存储与检索 |

### 2.3 工程工具

| 工具 | 用途 |
|------|------|
| **Maven** | 构建管理、多模块依赖 |
| **P3C PMD** | 阿里巴巴 Java 编码规范检查 |
| **Jacoco** | 代码覆盖率统计 |
| **Mockito** | 单元测试 Mock 框架 |
| **Lombok** | 减少样板代码（@Data、@Slf4j 等） |

---

## 3. Maven 多模块架构

### 3.1 模块依赖关系

```
smart-starter（启动模块）
    ↓ 依赖
smart-interfaces（接口层：Controller + DTO）
    ↓ 依赖
smart-application（应用层：业务编排）
    ↓ 依赖
smart-infrastructure（基础设施层：配置、RAG、Kafka、Redis、ES）
    ↓ 依赖
smart-domain（领域层：实体、Mapper）
    ↓ 依赖
smart-common（通用层：常量、枚举、异常、工具）
```

### 3.2 各模块职责

| 模块 | 职责 | 关键内容 |
|------|------|----------|
| `smart-common` | 通用基础 | 常量、枚举、异常体系、统一返回Result、工具类 |
| `smart-domain` | 领域模型 | 实体类（MyBatis Plus 注解）、Mapper 接口、TypeHandler |
| `smart-infrastructure` | 基础设施 | 双数据源配置、Redis/Kafka/ES 配置、RAG 管道、缓存服务 |
| `smart-application` | 应用服务 | 业务流程编排、规则引擎、CRUD 应用服务 |
| `smart-interfaces` | 接口适配 | REST Controller、请求/响应 DTO、全局异常处理 |
| `smart-starter` | 启动入口 | SpringBootApplication、配置文件、日志配置 |

### 3.3 为什么这样分层？

这种分层参考了 **DDD（领域驱动设计）** 的分层架构思想：

1. **依赖方向单一**：上层依赖下层，下层不知道上层的存在
2. **职责隔离**：Controller 不直接操作数据库，Application Service 编排业务流程
3. **可测试性**：每层可以独立测试，通过 Mock 隔离依赖
4. **可替换性**：切换数据库只需改 infrastructure 层，不影响业务逻辑

---

## 4. DDD 分层设计详解

### 4.1 smart-common — 通用层

**设计思路**：抽取所有模块共用的基础组件，避免循环依赖。

```
com.smart.common/
├── constants/      ← 常量类（Kafka Topic、Redis Key、缓存过期时间）
├── enums/          ← 业务枚举（设备类型、场景类型、决策状态等）
├── exception/      ← 统一异常体系（BizException + ErrorCode 枚举）
├── result/         ← 统一返回格式（Result<T> + PageResult<T>）
└── utils/          ← 工具类（JsonUtil）
```

**学习要点**：
- `ErrorCode` 枚举统一管理错误码，业务错误码 2xxxx，系统错误码 1xxxx
- `BizException` 继承 RuntimeException，可以被全局异常处理器捕获
- `Result<T>` 是前后端交互的统一格式：`{ code, message, data }`

### 4.2 smart-domain — 领域层

**设计思路**：定义业务领域模型，与数据库表一一对应。

```
com.smart.domain/
├── entity/         ← 9 个实体类（对应 8 个 MySQL 表 + 1 个 PostgreSQL 表）
├── mapper/         ← 8 个 MySQL Mapper 接口
│   └── chunk/      ← 1 个 PostgreSQL Mapper（独立子包，用于双数据源分包扫描）
└── handler/        ← PgVectorTypeHandler（pgvector ↔ float[] 类型转换）
```

**学习要点**：
- `@TableName("t_xxx")` — 指定数据库表名
- `@TableId(type = IdType.ASSIGN_ID)` — 雪花算法生成分布式唯一 ID
- `@TableField(fill = FieldFill.INSERT)` — 插入时自动填充字段（如 createTime）
- `KnowledgeChunk` 在 PostgreSQL 中，其 Mapper 放在 `mapper.chunk` 子包，便于双数据源分包扫描

### 4.3 smart-infrastructure — 基础设施层

**设计思路**：所有与外部系统（数据库、缓存、消息队列、AI 服务）的交互都在这一层。

```
com.smart.infrastructure/
├── config/         ← 数据源、Redis、Kafka、ES、Spring AI 配置
├── rag/            ← RAG 管道（文档加载→分片→向量化→存储→检索）
├── kafka/          ← Kafka 消费者和生产者
├── cache/          ← Redis 缓存服务（通用操作、场景缓存、决策缓存）
└── log/            ← 决策日志（ES 日志实体、服务、AOP 切面）
```

**学习要点**：
- **双数据源**：`MysqlDataSourceConfig` 和 `PostgresDataSourceConfig` 各自创建独立的 DataSource → SqlSessionFactory → SqlSessionTemplate
- **Redis 序列化**：key 用 StringRedisSerializer（人类可读），value 用 Jackson JSON 序列化
- **RAG 管道**：DocumentLoader → TextSplitter → EmbeddingService → VectorStoreService → RagPipelineService

### 4.4 smart-application — 应用层

**设计思路**：编排业务流程，协调领域层和基础设施层完成业务操作。

```
com.smart.application.service/
├── DecisionAppService.java     ← 核心：AI 决策流程编排（4 个阶段）
├── RuleEngineService.java      ← 规则引擎：条件匹配
├── StationAppService.java      ← 电站 CRUD
├── DeviceAppService.java       ← 设备 CRUD
├── SceneAppService.java        ← 场景 CRUD（含缓存刷新）
└── KnowledgeAppService.java    ← 知识库管理（上传→RAG入库）
```

**学习要点**：
- `DecisionAppService.processDeviceData()` 是核心方法，包含完整的四阶段决策流程
- `RuleEngineService` 采用策略模式思想，根据 conditionType 分派不同的匹配逻辑
- `SceneAppService.create()` 使用 `@Transactional` 保证场景和规则的事务一致性

### 4.5 smart-interfaces — 接口层

**设计思路**：处理 HTTP 请求，做 DTO 转换，不包含业务逻辑。

```
com.smart.interfaces/
├── rest/           ← 6 个 Controller（Swagger 注解完整）
├── dto/
│   ├── request/    ← 请求 DTO（含 JSR 303 校验注解）
│   ├── response/   ← 响应 DTO
│   └── cache/      ← 缓存专用 DTO
└── handler/        ← GlobalExceptionHandler（全局异常处理）
```

**学习要点**：
- Controller 只做三件事：①参数校验 ②调用 AppService ③DTO 转换
- `@Valid` + `@NotBlank`/`@NotNull` 实现声明式参数校验
- `GlobalExceptionHandler` 统一捕获异常，返回标准 Result 格式
- `SimulateController` 使用 `@Profile("dev")` 注解，仅在开发环境生效

---

## 5. 核心业务流程

### 5.1 决策流程（最核心，面试必问）

```
设备数据到达
    │
    ▼
┌─────────────────────────────────────────┐
│ 阶段1：规则匹配 (RULE_MATCH)            │
│                                         │
│ 1. 从 Redis 缓存加载电站启用的场景列表    │
│ 2. 遍历每个场景的规则条件                 │
│ 3. 用 RuleEngineService 匹配设备数据     │
│ 4. 找到第一个匹配的场景                   │
│                                         │
│ 未匹配 → 结束流程                        │
│ 匹配到 → 创建 DecisionRecord → 进入阶段2 │
└───────────────────┬─────────────────────┘
                    ▼
┌─────────────────────────────────────────┐
│ 阶段2：RAG 检索 (RAG_RETRIEVAL)         │
│                                         │
│ 1. 根据场景和设备数据构建查询语句         │
│ 2. RagPipelineService.retrieve() 检索    │
│    - 文本向量化                          │
│    - pgvector 余弦相似度 top-k 检索      │
│ 3. 返回相关知识片段                      │
└───────────────────┬─────────────────────┘
                    ▼
┌─────────────────────────────────────────┐
│ 阶段3：AI 推理 (AI_INFERENCE)           │
│                                         │
│ 1. 构建 Prompt（场景描述+设备数据+知识片段）│
│ 2. 调用 Spring AI ChatClient            │
│ 3. LLM 返回决策建议                     │
│ 4. 解析响应，提取建议和置信度            │
└───────────────────┬─────────────────────┘
                    ▼
┌─────────────────────────────────────────┐
│ 阶段4：指令下发 (COMMAND_DISPATCH)      │
│                                         │
│ 1. 更新 DecisionRecord 状态为 COMPLETED  │
│ 2. Kafka 发送决策结果消息                │
│ 3. Redis 缓存决策记录                   │
│ 4. ES 记录全链路日志                    │
└─────────────────────────────────────────┘
```

### 5.2 RAG 入库流程

```
上传知识文档
    │
    ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ DocumentLoader│ ──▶ │ TextSplitter  │ ──▶ │ Embedding    │ ──▶ │ VectorStore  │
│ 加载文档内容  │     │ 文本分片      │     │ Service      │     │ Service      │
│ (PDF/TXT等)  │     │ (500字/片     │     │ 文本→向量    │     │ 存入pgvector │
│              │     │  50字重叠)    │     │ (1536维)     │     │              │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
```

### 5.3 场景缓存-aside 模式

```
读请求：
  1. 先查 Redis 缓存
  2. 命中 → 直接返回
  3. 未命中 → 查 MySQL → 写入 Redis → 返回

写请求（创建/更新/删除场景）：
  1. 操作 MySQL
  2. 主动清除 Redis 缓存（evict）
  3. 下次读请求时缓存自动重建
```

---

## 6. RAG 知识检索管道

### 6.1 什么是 RAG？

**RAG（Retrieval-Augmented Generation）** = 检索增强生成

核心思想：不依赖 LLM 自身的知识，而是先从外部知识库检索相关信息，作为上下文传给 LLM，让 LLM 基于**最新、最相关**的信息生成回答。

### 6.2 RAG 在本项目中的作用

| 场景 | 不用 RAG | 用 RAG |
|------|---------|--------|
| 逆变器故障诊断 | LLM 可能给出通用建议 | 检索该型号逆变器的维修手册，给出精确建议 |
| 电池充放电策略 | LLM 不了解具体参数 | 检索电池技术规格书，结合实时 SOC 给出最优策略 |
| 削峰填谷决策 | LLM 不知道电价政策 | 检索最新电价政策文档，计算最优充放电时间 |

### 6.3 RAG 管道各组件详解

| 组件 | 类 | 职责 |
|------|------|------|
| **文档加载** | `DocumentLoader` | 读取 PDF/TXT/Markdown 等格式文件，提取纯文本 |
| **文本分片** | `TextSplitter` | 将长文本按 500 字分片，相邻片段重叠 50 字以保证语义连贯 |
| **向量化** | `EmbeddingService` | 调用 Embedding 模型（如 text-embedding-v3）将文本转为 1536 维向量 |
| **向量存储** | `VectorStoreService` | 将向量存入 PostgreSQL pgvector，支持余弦相似度检索 |
| **管道编排** | `RagPipelineService` | 串联以上步骤，提供 `ingest()`（入库）和 `retrieve()`（检索）方法 |

### 6.4 为什么用 pgvector 而不用 Pinecone/Milvus？

- **简单**：PostgreSQL 扩展，无需额外部署独立向量数据库
- **事务**：可以和其他 PostgreSQL 表在同一事务内操作
- **够用**：百万级向量检索性能满足本项目需求
- **IVFFlat 索引**：创建索引后检索延迟 < 50ms

---

## 7. 双数据源配置详解

### 7.1 为什么需要双数据源？

- **MySQL**：存储结构化业务数据（电站、设备、场景、决策记录等）
- **PostgreSQL + pgvector**：存储向量数据（文档分块的 embedding）

两个数据库各司其职，MySQL 擅长事务和关系查询，PostgreSQL + pgvector 擅长向量相似度检索。

### 7.2 实现方式

```java
// MySQL 数据源 — @Primary 标注为默认数据源
@Configuration
@MapperScan(
    basePackages = "com.smart.domain.mapper",           // 扫描主包
    sqlSessionFactoryRef = "mysqlSqlSessionFactory"
)
public class MysqlDataSourceConfig { ... }

// PostgreSQL 数据源 — 扫描 chunk 子包
@Configuration
@MapperScan(
    basePackages = "com.smart.domain.mapper.chunk",     // 只扫描 chunk 子包
    sqlSessionFactoryRef = "postgresSqlSessionFactory"
)
public class PostgresDataSourceConfig { ... }
```

**关键设计**：通过 Mapper 接口的**包路径**区分数据源，`mapper.chunk` 包下的 Mapper 走 PostgreSQL，其余走 MySQL。

### 7.3 PgVectorTypeHandler

pgvector 在数据库中存储为 `[0.1,0.2,0.3,...]` 格式的字符串，Java 中用 `float[]` 表示。

`PgVectorTypeHandler` 负责双向转换：
- **写入**：`float[] → "[0.1,0.2,0.3,...]"` 字符串
- **读取**：`"[0.1,0.2,0.3,...]"` 字符串 → `float[]`

---

## 8. Redis 缓存设计

### 8.1 缓存 Key 规范

| Key 格式 | 数据结构 | 用途 | TTL |
|----------|---------|------|-----|
| `smart:scenario:{stationId}` | String (JSON) | 电站下启用的场景列表 | 10 分钟 |
| `smart:scenario:rules:{sceneId}` | String (JSON) | 场景下的规则列表 | 10 分钟 |
| `smart:decision:{decisionId}` | String (JSON) | 决策记录缓存 | 1 小时 |
| `smart:device:status:{deviceId}` | String | 设备状态 | - |
| `smart:station:{stationId}` | String (JSON) | 电站信息缓存 | - |

### 8.2 缓存序列化配置

```
┌─────────────────┐
│   RedisConfig    │
├─────────────────┤
│ Key Serializer:  │ → StringRedisSerializer（人类可读，如 "smart:scenario:123"）
│ Value Serializer:│ → GenericJackson2JsonRedisSerializer（JSON格式，便于调试）
│ Hash Key:        │ → StringRedisSerializer
│ Hash Value:      │ → GenericJackson2JsonRedisSerializer
└─────────────────┘
```

### 8.3 Cache-Aside 模式（旁路缓存）

本项目使用经典的 Cache-Aside 模式：

**读取**：先读缓存 → 未命中则读 DB → 写入缓存 → 返回
**写入**：写 DB → 删缓存（不是更新缓存，避免脏数据）

`ScenarioCacheService` 是这个模式的典型实现：
```java
public List<SmartScene> getEnabledScenes(Long stationId) {
    // 1. 查缓存
    List<SmartScene> scenes = redisService.get(key);
    if (scenes != null) return scenes;    // 命中直接返回
    // 2. 查数据库
    scenes = smartSceneMapper.selectList(...);
    // 3. 写入缓存（设置 10 分钟过期）
    redisService.set(key, scenes, CacheExpire.TEN_MINUTES, TimeUnit.SECONDS);
    return scenes;
}
```

---

## 9. Kafka 消息驱动

### 9.1 消息流向

```
IoT 设备 ──publish──▶ [device-data Topic] ──consume──▶ DeviceDataConsumer
                                                              │
                                                    processDeviceData()
                                                              │
                                                              ▼
                     [decision-result Topic] ◀──publish── DecisionResultProducer
```

### 9.2 Topic 设计

| Topic | 生产者 | 消费者 | 消息体 |
|-------|--------|--------|--------|
| `smart-device-data` | IoT 网关 | DeviceDataConsumer | DeviceDataMessage |
| `smart-decision-result` | DecisionResultProducer | 下游执行系统 | DecisionResultMessage |
| `smart-decision-log` | DecisionLogService | 日志分析系统 | DecisionLog |

### 9.3 消费者设计要点

- **幂等性**：消费者应具备幂等处理能力，重复消费不会产生副作用
- **异常处理**：反序列化失败时记录日志，不阻塞消费
- **消费者组**：使用 `smart-group` 作为 Consumer Group，支持多实例并行消费

---

## 10. 规则引擎设计

### 10.1 规则匹配流程

```
设备数据 (DeviceDataMessage)
    │
    ▼
遍历场景的所有规则 (List<SceneRule>)
    │
    ├── 规则1: SOC > 90%          ← getActualValue() 从设备数据取 SOC 值
    ├── 规则2: 功率 < 5000W       ← compareValue() 比较实际值与阈值
    └── 规则3: 温度 <= 45°C
    │
    ▼
所有规则都满足 → 匹配成功（AND 逻辑）
任一规则不满足 → 匹配失败
```

### 10.2 条件类型与设备数据字段映射

| ConditionType | 枚举值 | 对应 DeviceDataMessage 字段 |
|--------------|--------|---------------------------|
| SOC | 1 | `data.getSoc()` |
| POWER | 2 | `data.getPower()` |
| VOLTAGE | 3 | `data.getVoltage()` |
| TEMPERATURE | 4 | `data.getTemperature()` |
| TIME_RANGE | 5 | TODO: 时间段判断 |

### 10.3 比较符号

| ValueSign | 符号 | BigDecimal compareTo |
|-----------|------|---------------------|
| GT | > | `compareTo > 0` |
| GTE | >= | `compareTo >= 0` |
| LT | < | `compareTo < 0` |
| LTE | <= | `compareTo <= 0` |
| EQ | == | `compareTo == 0` |
| BETWEEN | between | TODO: 解析范围值 |

---

## 11. 全链路日志追踪

### 11.1 日志架构

```
DecisionAppService
    │
    ├──@LogDecision(stage="RULE_MATCH")──▶ DecisionLogAspect
    │                                          │
    │                                    记录耗时+结果
    │                                          │
    │                                          ▼
    │                                    DecisionLogService
    │                                          │
    │                                          ▼
    │                                    Elasticsearch
    │
    ├──@LogDecision(stage="RAG_RETRIEVAL")
    ├──@LogDecision(stage="AI_INFERENCE")
    └──@LogDecision(stage="COMMAND_DISPATCH")
```

### 11.2 DecisionLog 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| decisionId | Long | 决策记录 ID |
| stationId | Long | 电站 ID |
| sceneId | Long | 场景 ID |
| stage | String | 决策阶段（RULE_MATCH / RAG_RETRIEVAL / AI_INFERENCE / COMMAND_DISPATCH） |
| input | String | 该阶段输入摘要 |
| output | String | 该阶段输出摘要 |
| costMs | Long | 该阶段耗时（毫秒） |
| success | Boolean | 是否成功 |
| errorMsg | String | 错误信息 |

### 11.3 AOP 切面实现

`DecisionLogAspect` 使用 `@Around` 环绕通知，自动记录方法执行时间和结果：
- 方法执行前记录开始时间
- 方法执行后计算耗时
- 异常时记录错误信息
- 最终将日志保存到 Elasticsearch

---

## 12. 代码规范与质量

### 12.1 阿里巴巴 P3C 规范

本项目集成了阿里巴巴 P3C PMD 插件，运行 `mvn pmd:check` 检查代码规范。

**常见规则**：
- `ClassMustHaveAuthorRule`：每个类必须有 `@author` 注解
- `UndefineMagicConstantRule`：不允许魔法值，必须定义为常量
- `RemoveCommentedCodeRule`：不允许注释掉的代码块

### 12.2 单元测试

| 测试类 | 所在模块 | 测试数量 | 覆盖内容 |
|--------|---------|----------|---------|
| RuleEngineServiceTest | smart-application | 7 | 各种条件匹配场景 |
| ScenarioCacheServiceTest | smart-infrastructure | 5 | 缓存命中/未命中、刷新 |
| DecisionAppServiceTest | smart-application | 4 | 决策流程各阶段 |
| DeviceDataConsumerTest | smart-infrastructure | 3 | Kafka 消费正常/异常 |
| KnowledgeAppServiceTest | smart-application | 5 | 知识库 CRUD |

### 12.3 测试技巧

```java
// 典型的 Mockito 测试结构
@ExtendWith(MockitoExtension.class)
class RuleEngineServiceTest {
    @InjectMocks
    private RuleEngineService ruleEngineService;  // 被测对象

    @Mock
    private SomeDependency dependency;             // Mock 依赖

    @Test
    void testSomeScenario() {
        // Given — 准备测试数据和 Mock 行为
        when(dependency.doSomething()).thenReturn(expected);
        // When — 执行被测方法
        var result = ruleEngineService.someMethod();
        // Then — 验证结果
        assertThat(result).isEqualTo(expected);
        verify(dependency).doSomething();          // 验证调用
    }
}
```

---

## 13. 面试高频问题与回答要点

### Q1: 为什么选择 RAG 而不是 Fine-tuning？

**回答要点**：
- RAG **无需重新训练模型**，只需更新知识库即可获取最新信息
- 设备手册、故障案例经常更新，RAG 能**实时反映最新知识**
- RAG 的回答可以**溯源**，能看到引用了哪些知识片段，增强可信度
- Fine-tuning 成本高、周期长，不适合快速迭代的项目

### Q2: 为什么用双数据源（MySQL + PostgreSQL）？

**回答要点**：
- **MySQL** 擅长 OLTP 场景：事务处理、关系查询、索引优化
- **PostgreSQL + pgvector** 提供原生的**向量索引**（IVFFlat），支持高效的余弦相似度检索
- 职责分离：业务数据和向量数据有完全不同的读写模式和查询需求
- MyBatis Plus 的 `@MapperScan` 通过**分包扫描**优雅地实现了双数据源

### Q3: 规则引擎如何与 AI 决策协作？

**回答要点**：
- **规则引擎是第一道防线**：快速、确定性强、延迟低
- 规则命中后才触发 AI 决策，**避免不必要的 LLM 调用**（节省成本）
- AI 生成的建议附带**置信度**，低于阈值时自动回退到规则执行
- 形成"**规则兜底 + AI 增强**"的双保险机制

### Q4: Redis 缓存一致性如何保证？

**回答要点**：
- 使用 **Cache-Aside** 模式：先更新 DB，再删除缓存
- 设置缓存 **TTL**（如 10 分钟），即使删除失败也会自动过期
- 场景配置变更时**主动清除**相关缓存
- 对于强一致性场景，可以引入 **延迟双删** 策略

### Q5: Kafka 消息消费失败如何处理？

**回答要点**：
- 反序列化失败：**记录日志，跳过**，不阻塞后续消息
- 业务处理失败：**记录 ES 日志** + ERROR 日志
- 可以配置**死信队列（DLQ）**，失败消息进入死信队列后续人工处理
- Consumer 端做**幂等处理**，重复消费不会产生副作用

### Q6: 为什么选择 Elasticsearch 存储日志？

**回答要点**：
- 支持**全文检索**和**结构化查询**（按电站、设备、时间范围过滤）
- 天然支持**时间序列**数据，按月滚动索引
- 内置**聚合分析**能力（如统计各阶段平均耗时）
- 配合 Kibana 可以快速搭建**监控看板**

### Q7: Spring AI 如何集成 LLM？

**回答要点**：
- Spring AI 提供了**统一抽象**，支持 OpenAI / 通义千问 / DeepSeek / Ollama
- 通过**环境变量**切换不同模型，无需改代码
- `EmbeddingModel` 生成文本向量，`ChatClient` 调用 LLM 生成回答
- 配置 `base-url` 即可对接任何 OpenAI 兼容接口

### Q8: 这个项目的扩展方向？

**回答要点**：
- **置信度阈值自适应**：根据历史决策反馈动态调整阈值
- **多模型切换**：不同场景使用不同 LLM（如故障诊断用专业模型）
- **知识库版本管理**：支持知识文档的版本控制和回滚
- **决策审计**：完整的决策过程可追溯、可回放
- **A/B 测试**：规则决策 vs AI 决策的效果对比

---

## 14. 扩展阅读

### 推荐学习资源

| 主题 | 资源 |
|------|------|
| Spring Boot 3 | [官方文档](https://docs.spring.io/spring-boot/docs/3.2.x/reference/html/) |
| Spring AI | [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/) |
| MyBatis Plus | [MyBatis Plus 官方文档](https://baomidou.com/) |
| pgvector | [pgvector GitHub](https://github.com/pgvector/pgvector) |
| RAG 原理 | 搜索"Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks" |
| DDD 分层架构 | 《实现领域驱动设计》— Vaughn Vernon |
| 阿里巴巴 Java 开发手册 | 搜索"阿里巴巴 Java 开发手册（嵩山版）" |

### 项目相关命令速查

```bash
# 构建项目（跳过测试）
mvn clean install -DskipTests

# 运行 P3C 检查
mvn pmd:check

# 运行单元测试
mvn test

# 运行项目
cd smart-starter && mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 初始化数据库
make db-init

# 访问 Swagger UI
# http://localhost:8080/swagger-ui.html
```

---

> **祝你面试顺利！** 建议重点掌握决策流程、RAG 管道、双数据源配置和 Redis 缓存设计，这些是面试官最关注的亮点。
