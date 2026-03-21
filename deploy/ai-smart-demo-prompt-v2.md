请帮我初始化一个 Spring Boot 项目，项目名称为 ai-smart-demo，包名 com.aismart.demo。这是一个基于 RAG 的 AI 智能决策系统，应用于光伏储能领域。

应用场景：IoT 设备（逆变器、电池、热泵、充电桩等）上报工作数据，系统通过 Kafka 消费数据，匹配电站绑定的智能化场景，先走规则引擎判断触发条件，命中规则且开启了 AI 决策的场景，再走 RAG 检索知识库（设备手册、故障案例、运维规范），由 LLM 生成智能决策建议。系统同时保留规则兜底机制，AI 置信度低时回退到规则执行。

这个项目是面试用的，要求能快速跑起来、代码结构清晰、方便我后续逐步填充实现。

## 技术栈

- Java 17+
- Spring Boot 3.2+
- Spring AI（LLM 集成、Embedding、RAG，使用 OpenAI 兼容接口）
- MyBatis Plus 3.5+（不要用 JPA）
- MySQL 8（业务主库：电站、设备、场景、条件、任务、AI 决策记录、知识文档元数据）
- PostgreSQL 16 + pgvector（专用向量库：存储文档分块 embedding）
- Elasticsearch 8（智能化执行日志，全链路追踪）
- Apache Kafka（设备数据消费 + 决策结果发布）
- Redis 7（缓存场景配置 + 决策结果 + 反向索引）
- SpringDoc OpenAPI（Swagger 文档）
- Maven 多模块
- Docker Compose（使用已有的基础设施环境）

## 不需要的东西

- 不要 Spring Security / JWT / 任何认证
- 不要 users 表
- 不要 Flyway
- 不要生成 docker-compose.yml，我已有现成的基础设施环境

## 已有基础设施环境（重要，所有连接配置必须与此一致）

我本地已有 Docker Compose 环境，各组件连接信息如下：

| 组件 | 端口（宿主机） | 用户名 | 密码 | 数据库/说明 |
|------|---------------|--------|------|------------|
| MySQL 8 | 3306 | root | root123 | 项目使用 ai_smart 库（需手动建库建表） |
| PostgreSQL 16 + pgvector | 5432 | appuser | apppass | 项目使用 appdb 库（已有，在此库内建表和 pgvector 扩展） |
| Redis 7 | 16379 | - | redis123 | 有密码 |
| Kafka (Confluent) | 19092 | - | - | Zookeeper 模式，宿主机通过 19092 访问 |
| Elasticsearch 8 | 9200 | - | - | xpack.security 已关闭 |
| Kibana | 5601 | - | - | 可选 UI |
| Kafka UI | 18080 | - | - | 可选 UI |
| Redis Insight | 5540 | - | - | 可选 UI |

注意事项：
- Kafka 宿主机端口是 19092，不是 9092
- Redis 宿主机端口是 16379，不是 6379，且有密码 redis123
- PostgreSQL 用户是 appuser/apppass，数据库用已有的 appdb
- MySQL 需要新建 ai_smart 数据库和所有表

## Maven 多模块

ai-smart-demo/（父 pom）
├── smart-common/           # 通用：常量、枚举、异常、统一返回
├── smart-domain/           # 领域：实体、Mapper
├── smart-infrastructure/   # 基础设施：RAG、Kafka、Redis、ES、多数据源配置
├── smart-application/      # 应用服务：业务编排
├── smart-interfaces/       # Controller、DTO
└── smart-starter/          # 启动模块，汇聚依赖

父 pom 用 dependencyManagement 统一管理版本。

## 各模块详细包结构

### smart-common
com.aismart.demo.common/
├── constants/          # KafkaTopics、RedisKeys（统一管理所有 Redis key 的前缀和格式）、CacheExpire
├── enums/              # DeviceType、ScenarioType、DecisionStatus、DecisionStage、ConditionType、StationStatus、ValueSign
├── exception/          # BizException、ErrorCode 枚举
├── result/             # Result<T>、PageResult<T>
└── utils/              # JsonUtil（Jackson 封装）

RedisKeys 常量类中定义所有 key 格式：
```java
public class RedisKeys {
    /** 正向缓存：设备维度的场景配置。Hash 结构，hashKey=sceneId(String), hashValue=SmartSceneDTO(JSON) */
    public static final String EQUIP_SCENE = "smart:equipScene:%s";
    /** 反向索引：场景关联了哪些设备。Set 结构，member=equipSn */
    public static final String SCENE_EQUIPS = "smart:sceneEquips:%s";
    /** 执行状态：独立存储，不混入场景缓存。String 结构 */
    public static final String EXECUTE_STATUS = "smart:executeStatus:%s:%s";
    /** 决策结果缓存 */
    public static final String DECISION_LATEST = "smart:decision:latest:%s:%s";

    public static String equipScene(String equipSn) { return String.format(EQUIP_SCENE, equipSn); }
    public static String sceneEquips(Long sceneId) { return String.format(SCENE_EQUIPS, sceneId); }
    public static String executeStatus(Long sceneId, String equipSn) { return String.format(EXECUTE_STATUS, sceneId, equipSn); }
    public static String decisionLatest(String deviceId, Long sceneId) { return String.format(DECISION_LATEST, deviceId, sceneId); }
}
```

### smart-domain
com.aismart.demo.domain/
├── station/
│   ├── entity/         # Station（MySQL）
│   └── mapper/         # StationMapper
├── device/
│   ├── entity/         # Device（MySQL）
│   └── mapper/         # DeviceMapper
├── scene/
│   ├── entity/         # SmartScene（MySQL）
│   └── mapper/         # SmartSceneMapper
├── condition/
│   ├── entity/         # SmartCondition（MySQL）
│   └── mapper/         # SmartConditionMapper
├── task/
│   ├── entity/         # SmartTask（MySQL）
│   └── mapper/         # SmartTaskMapper
├── knowledge/
│   ├── entity/         # KnowledgeDocument（MySQL）
│   └── mapper/         # KnowledgeDocumentMapper
├── chunk/
│   ├── entity/         # DocumentChunk（PostgreSQL，embedding 字段用自定义 TypeHandler 处理 pgvector 类型）
│   └── mapper/         # DocumentChunkMapper
└── decision/
    ├── entity/         # AiDecision、DecisionReference（MySQL）
    └── mapper/         # AiDecisionMapper、DecisionReferenceMapper

所有实体用 MyBatis Plus 注解（@TableName, @TableId, @TableField），id 雪花算法。
双数据源：MySQL Mapper 和 PostgreSQL Mapper 分包扫描，各自独立 SqlSessionFactory。

### smart-infrastructure
com.aismart.demo.infrastructure/
├── config/
│   ├── MysqlDataSourceConfig.java      # MySQL 数据源 + SqlSessionFactory，扫描除 chunk 外的所有 mapper 包
│   ├── PostgresDataSourceConfig.java   # PostgreSQL 数据源 + SqlSessionFactory，扫描 chunk.mapper 包
│   ├── RedisConfig.java                # 见下方 Redis 配置详细说明
│   ├── KafkaConfig.java                # Consumer/Producer 配置
│   ├── ElasticsearchConfig.java        # ES RestClient 配置
│   └── SpringAiConfig.java             # Spring AI 相关 Bean 配置
├── rag/
│   ├── DocumentLoader.java             # 文档加载（支持 PDF、TXT、Markdown）
│   ├── TextSplitter.java               # 文本分块（按 token 数，支持 overlap）
│   ├── EmbeddingService.java           # 调用 Spring AI EmbeddingModel 生成向量
│   ├── VectorStoreService.java         # pgvector 存取封装，语义检索 top-k
│   └── RagPipelineService.java         # RAG 完整流程编排：检索 → 构建 context → 调 LLM
├── kafka/
│   ├── DeviceDataConsumer.java         # 监听 device-data topic，解析消息，触发决策流程
│   ├── DecisionResultProducer.java     # 发送决策结果到 decision-result topic
│   └── message/
│       ├── DeviceDataMessage.java      # 设备上报数据消息体
│       └── DecisionResultMessage.java  # 决策结果消息体
├── cache/
│   ├── RedisService.java               # Redis 操作统一封装（String/Hash/Set 操作）
│   ├── ScenarioCacheService.java       # 场景缓存管理（正向缓存 + 反向索引 + 执行状态）
│   └── DecisionCacheService.java       # 决策结果缓存（TTL 可配）
└── log/
    ├── DecisionLog.java                # 执行日志实体（对应 ES 文档结构）
    ├── DecisionLogService.java         # ES 日志写入和查询封装
    └── DecisionLogAspect.java          # AOP 切面，自动记录决策执行全链路日志

### smart-application
com.aismart.demo.application/
├── StationAppService.java      # 电站管理
├── DeviceAppService.java       # 设备管理
├── SceneAppService.java        # 场景配置管理（CRUD + 刷新 Redis 正向缓存和反向索引）
├── KnowledgeAppService.java    # 知识库管理编排（上传→分块→embedding→入库）
├── DecisionAppService.java     # 决策流程编排（规则匹配→AI决策→存储→发Kafka→记日志）
└── RuleEngineService.java      # 规则引擎：根据设备数据和场景条件判断是否触发

### smart-interfaces
com.aismart.demo.interfaces/
├── rest/
│   ├── StationController.java
│   ├── DeviceController.java
│   ├── SceneController.java
│   ├── KnowledgeController.java
│   ├── DecisionController.java
│   ├── DecisionLogController.java
│   └── SimulateController.java     # 仅 dev profile，模拟设备数据和初始化示例数据
└── dto/
    ├── request/    # StationCreateReq, DeviceCreateReq, SceneCreateReq, KnowledgeUploadReq, DecisionQueryReq 等
    ├── response/   # StationResp, DeviceResp, SceneDetailResp, DecisionDetailResp, DecisionLogResp 等
    └── cache/      # SmartSceneDTO（缓存专用 DTO）

## Redis 缓存设计（重点，按现有业务方案沿用并扩展）

### RedisConfig 配置

```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // key 和 hashKey 统一用 StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        // value 和 hashValue 用 Jackson 序列化
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(om);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
```

hashKey 用 StringRedisSerializer，所以存取时 sceneId 统一转为 String：String.valueOf(sceneId)。

### 缓存结构设计

#### 1. 正向缓存：设备维度的场景配置（沿用现有方案）

```
Redis Key:    smart:equipScene:{equipSn}
Redis Type:   Hash
Hash Key:     sceneId（String 类型，如 "123"）
Hash Value:   SmartSceneDTO（JSON）
```

SmartSceneDTO 定义（缓存专用，从现有 DTO 扩展，不含 executeStatus）：
```java
public class SmartSceneDTO implements Serializable {
    private Long sceneId;
    private String sceneName;
    private String sceneDesc;
    /** AI决策是否启用;0.仅规则 1.AI辅助决策 */
    private Integer aiEnabled;
    /** AI决策时的prompt模板 */
    private String aiQueryTemplate;
    /** 触发条件列表 */
    private List<SmartCondition> smartConditionList;
    /** 设备执行任务列表 */
    private List<SmartTask> smartTaskList;
}
```

注意：相比现有 DTO，去掉了 executeStatus（拆到独立 key），新增了 sceneId、sceneDesc、aiEnabled、aiQueryTemplate。

#### 2. 反向索引：场景关联了哪些设备（新增）

```
Redis Key:    smart:sceneEquips:{sceneId}
Redis Type:   Set
Members:      equipSn（如 "INV-001", "BAT-002"）
```

用途：场景修改/删除时，通过此索引快速找到需要刷新的设备缓存。

#### 3. 执行状态：独立存储（从 SmartSceneDTO 中拆出）

```
Redis Key:    smart:executeStatus:{sceneId}:{equipSn}
Redis Type:   String
Value:        0 或 1（0-未执行 1-已执行）
TTL:          无（由业务逻辑控制重置）
```

#### 4. 决策结果缓存

```
Redis Key:    smart:decision:latest:{deviceId}:{sceneId}
Redis Type:   String
Value:        AiDecision JSON
TTL:          30 分钟（可配）
```

### ScenarioCacheService 核心方法

```java
@Service
public class ScenarioCacheService {

    /**
     * 根据设备序列号获取其关联的所有场景配置
     * Consumer 消费设备数据时调用此方法
     */
    public Map<String, SmartSceneDTO> getScenesByEquipSn(String equipSn) {
        // opsForHash.entries(RedisKeys.equipScene(equipSn))
        // 缓存未命中时从 DB 加载并回填
    }

    /**
     * 预热缓存：启动时或手动触发
     * 1. 查所有启用的场景及其 conditions 和 tasks
     * 2. 按设备维度聚合，写入正向缓存
     * 3. 按场景维度聚合，写入反向索引
     */
    public void warmUpCache() { }

    /**
     * 场景变更时刷新缓存
     * 1. 从反向索引 smart:sceneEquips:{sceneId} 获取所有关联设备
     * 2. 遍历设备，更新每个设备正向缓存中该 sceneId 的数据
     * 3. 如果场景关联的设备有变化，同步更新反向索引
     */
    public void refreshSceneCache(Long sceneId) { }

    /**
     * 删除场景缓存
     * 1. 从反向索引获取所有关联设备
     * 2. 删除每个设备正向缓存中该 sceneId 的 hash entry
     * 3. 删除反向索引 key
     */
    public void removeSceneCache(Long sceneId) { }

    /**
     * 获取设备某场景的执行状态
     */
    public Integer getExecuteStatus(Long sceneId, String equipSn) { }

    /**
     * 更新执行状态（独立 key，不影响场景缓存）
     */
    public void updateExecuteStatus(Long sceneId, String equipSn, Integer status) { }
}
```

## 数据库初始化脚本（手动执行，不走 Docker 自动初始化）

创建 deploy/sql/ 目录，提供 SQL 脚本供手动执行：

### deploy/sql/mysql-init.sql

```sql
-- 在已有 MySQL 中创建项目数据库和所有表
-- 执行方式：mysql -h localhost -P 3306 -uroot -proot123 < deploy/sql/mysql-init.sql

CREATE DATABASE IF NOT EXISTS ai_smart DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ai_smart;

-- ========== 基础数据表 ==========

CREATE TABLE smart_station (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '电站id',
  station_name varchar(64) NOT NULL COMMENT '电站名称',
  station_code varchar(32) DEFAULT NULL COMMENT '电站编码',
  region_code varchar(50) DEFAULT NULL COMMENT '地区编码',
  address varchar(255) DEFAULT NULL COMMENT '详细地址',
  longitude decimal(10,7) DEFAULT NULL COMMENT '经度',
  latitude decimal(10,7) DEFAULT NULL COMMENT '纬度',
  timezone_code varchar(50) DEFAULT NULL COMMENT '时区',
  capacity decimal(12,2) DEFAULT NULL COMMENT '装机容量(kW)',
  station_type tinyint DEFAULT NULL COMMENT '电站类型;1.户用 2.工商业 3.地面',
  status tinyint NOT NULL DEFAULT 1 COMMENT '状态;0.离线 1.正常 2.告警 3.故障',
  owner_name varchar(64) DEFAULT NULL COMMENT '业主姓名',
  owner_phone varchar(20) DEFAULT NULL COMMENT '业主电话',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_station_code (station_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电站表';

CREATE TABLE smart_device (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '设备id',
  station_id bigint NOT NULL COMMENT '所属电站id',
  equip_sn varchar(128) NOT NULL COMMENT '设备序列号',
  equip_name varchar(64) DEFAULT NULL COMMENT '设备名称',
  gsn varchar(32) DEFAULT NULL COMMENT '采集器序列号',
  device_type tinyint NOT NULL COMMENT '设备类型;2.逆变器 3.智能开关 6.电表 7.电池 8.热泵 10.充电桩 11.灯泡 12.电网 13.发电机',
  model varchar(64) DEFAULT NULL COMMENT '设备型号',
  manufacturer varchar(64) DEFAULT NULL COMMENT '生产厂商',
  rated_power decimal(12,2) DEFAULT NULL COMMENT '额定功率(W)',
  rated_capacity decimal(12,2) DEFAULT NULL COMMENT '额定容量(kWh)，电池适用',
  status tinyint NOT NULL DEFAULT 1 COMMENT '状态;0.离线 1.正常 2.告警 3.故障',
  online_time datetime DEFAULT NULL COMMENT '上线时间',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_equip_sn (equip_sn),
  KEY idx_station_id (station_id),
  KEY idx_device_type (device_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设备表';

-- ========== 智能化场景表（基于现有业务适配）==========

CREATE TABLE smart_scene (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '场景id',
  station_id bigint DEFAULT 0 COMMENT '电站id',
  station_name varchar(64) DEFAULT NULL COMMENT '电站名',
  scene_name varchar(64) NOT NULL COMMENT '场景名',
  scene_desc varchar(500) DEFAULT NULL COMMENT '场景描述，供AI理解场景语义',
  scene_status tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否启用;1.启用 0.未启用',
  ai_enabled tinyint(1) NOT NULL DEFAULT 0 COMMENT 'AI决策是否启用;0.仅规则 1.AI辅助决策',
  ai_query_template text DEFAULT NULL COMMENT 'AI决策时的prompt模板',
  condition_types varchar(255) DEFAULT NULL COMMENT '所有触发条件类型',
  task_types varchar(255) DEFAULT NULL COMMENT '所有执行任务类型',
  condition_overview varchar(255) DEFAULT NULL COMMENT '条件概述',
  task_overview varchar(255) DEFAULT NULL COMMENT '任务概述',
  type tinyint(1) NOT NULL DEFAULT 0 COMMENT '智能场景类型 0:正式场景 1:公共模板 2:用户模板',
  user_id bigint DEFAULT NULL COMMENT '用户ID',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_station_id (station_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='智能场景表';

CREATE TABLE smart_condition (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '触发条件id',
  scene_id bigint NOT NULL COMMENT '场景id',
  equip_sn varchar(32) DEFAULT NULL COMMENT '设备序列号',
  equip_name varchar(32) DEFAULT NULL COMMENT '设备名',
  condition_type int NOT NULL COMMENT '条件类型;0.电池SOC 1.光伏发电 2.买电功率 3.卖电功率 4.电表 5.日发电量 6.月发电量 7.日买电量 8.月买电量 9.日卖电量 10.月卖电量 11.买电电价 12.卖电电价 13.天气 14.时间 15.急停开关 16.采集器 17.日出 18.日落 19.实时电价',
  condition_code varchar(32) DEFAULT NULL COMMENT '条件编码，用于程序匹配，如 BATTERY_SOC, PV_POWER, BUY_PRICE',
  value_sign int DEFAULT NULL COMMENT '数据符号 0.小于 1.等于 2.大于 3.大于等于 4.不等于',
  threshold_value decimal(24,8) DEFAULT NULL COMMENT '数据值',
  value_unit varchar(8) DEFAULT NULL COMMENT '数据单位;4.W 8.kWh 13.% CURRENCY.货币',
  switch_on tinyint(1) DEFAULT NULL COMMENT '开关状态;0.关闭 1.打开',
  meter_status tinyint(1) DEFAULT NULL COMMENT '电表状态；0.卖电 1.买电',
  di_open_num varchar(32) DEFAULT NULL COMMENT '采集器DI开启状态下标',
  di_close_num varchar(32) DEFAULT NULL COMMENT '采集器DI关闭状态下标',
  condition_week char(7) DEFAULT NULL COMMENT '设定的星期;1111111代表周一到周日',
  condition_start_time char(64) DEFAULT NULL COMMENT '开始时间;HH:mm',
  condition_end_time char(64) DEFAULT NULL COMMENT '停止时间;HH:mm',
  title varchar(255) DEFAULT NULL COMMENT '约束条件标题',
  summary varchar(255) DEFAULT NULL COMMENT '约束条件描述',
  region_code varchar(50) DEFAULT NULL COMMENT '地区code',
  product_id varchar(50) DEFAULT NULL COMMENT '所选产品类型id',
  time_interval int DEFAULT NULL COMMENT '时间间隔 单位:min',
  timezone_code varchar(50) DEFAULT NULL,
  price_type int DEFAULT NULL COMMENT '价格类型',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_scene_id (scene_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='智能场景触发条件表';

CREATE TABLE smart_task (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '任务id',
  gsn varchar(32) DEFAULT NULL COMMENT '采集器序列号',
  equip_sn varchar(128) DEFAULT NULL COMMENT '设备序列号',
  scene_id bigint NOT NULL COMMENT '场景id',
  equip_name varchar(64) DEFAULT NULL COMMENT '设备名',
  device_type tinyint(1) NOT NULL COMMENT '设备类型;2.逆变器 3.智能开关 6.电表 7.电池 8.热泵 10.充电桩 11.灯泡 12.电网 13.发电机',
  switch_on tinyint(1) DEFAULT NULL COMMENT '开关状态;0-关闭 1-开启',
  work_mode int DEFAULT NULL COMMENT '工作模式 0:不运行 1:单制热 2:单制冷 3:单热水 4:自动模式',
  heat_or_refrigerate tinyint(1) DEFAULT NULL COMMENT '热泵 0.制冷 1.制热',
  color_axis varchar(16) DEFAULT NULL COMMENT '颜色坐标轴 255,255,255',
  bright int DEFAULT NULL COMMENT '亮度 0~100%',
  temperature decimal(24,1) DEFAULT NULL COMMENT '温度',
  max_power decimal(24,2) DEFAULT NULL COMMENT '最大功率',
  do_open_num varchar(32) DEFAULT NULL COMMENT 'DO开启状态下标',
  do_close_num varchar(32) DEFAULT NULL COMMENT 'DO关闭状态下标',
  title varchar(255) DEFAULT NULL COMMENT '任务标题',
  summary varchar(255) DEFAULT NULL COMMENT '任务描述',
  sell_or_buy tinyint(1) DEFAULT NULL COMMENT '买卖状态 1.买 0.卖',
  device_status tinyint(1) DEFAULT NULL COMMENT '设备状态;电池 1.充电 0.放电',
  target_soc int DEFAULT NULL COMMENT '目标soc',
  execute_status tinyint NOT NULL DEFAULT 0 COMMENT '执行状态：0-未执行 1-已执行',
  execute_source tinyint NOT NULL DEFAULT 0 COMMENT '执行来源：0-规则触发 1-AI建议 2-手动',
  ai_suggestion text DEFAULT NULL COMMENT 'AI给出的执行建议',
  ai_confidence decimal(5,4) DEFAULT NULL COMMENT 'AI决策置信度 0~1',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_scene_id (scene_id),
  KEY idx_task_equip_sn_scene (equip_sn, scene_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='智能设备任务表';

-- ========== AI 相关表 ==========

CREATE TABLE smart_knowledge_document (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '文档id',
  title varchar(200) NOT NULL COMMENT '文档标题',
  file_name varchar(200) NOT NULL COMMENT '文件名',
  file_type varchar(20) NOT NULL COMMENT '文件类型 PDF/TXT/MD',
  file_size bigint NOT NULL COMMENT '文件大小(bytes)',
  chunk_count int DEFAULT 0 COMMENT '分块数量',
  doc_category varchar(50) DEFAULT NULL COMMENT '文档分类：DEVICE_MANUAL/FAULT_CASE/OPERATION_GUIDE/ENERGY_POLICY',
  status varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSING/COMPLETED/FAILED',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='知识库文档表';

CREATE TABLE smart_ai_decision (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '决策id',
  trace_id varchar(64) NOT NULL COMMENT '链路追踪ID',
  station_id bigint DEFAULT NULL COMMENT '电站id',
  scene_id bigint DEFAULT NULL COMMENT '关联场景id',
  device_id varchar(50) NOT NULL COMMENT '设备ID',
  device_type varchar(50) NOT NULL COMMENT '设备类型',
  device_data json NOT NULL COMMENT '触发时的设备数据快照',
  rule_matched tinyint(1) DEFAULT 0 COMMENT '规则是否匹配;0.未匹配 1.已匹配',
  query_text text COMMENT '发给LLM的完整query',
  retrieved_context text COMMENT 'RAG检索到的上下文',
  ai_response text COMMENT 'LLM返回的完整响应',
  suggestion text COMMENT 'AI决策建议摘要',
  confidence decimal(5,4) DEFAULT NULL COMMENT '置信度 0~1',
  status varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSING/COMPLETED/FAILED/IGNORED',
  adopted tinyint(1) DEFAULT NULL COMMENT '建议是否被采纳;0.否 1.是',
  cost_millis int DEFAULT NULL COMMENT '决策耗时(ms)',
  create_time datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_trace_id (trace_id),
  KEY idx_device_id (device_id),
  KEY idx_scene_id (scene_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI决策记录表';

CREATE TABLE smart_decision_reference (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '引用id',
  decision_id bigint NOT NULL COMMENT '决策id',
  chunk_id bigint NOT NULL COMMENT '文档分块id',
  chunk_content text COMMENT '引用的知识片段内容',
  similarity_score double NOT NULL COMMENT '相似度分数',
  create_time datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_decision_id (decision_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI决策知识引用表';
```

### deploy/sql/postgres-init.sql

```sql
-- 在已有 PostgreSQL 的 appdb 库中创建 pgvector 扩展和向量表
-- 执行方式：PGPASSWORD=apppass psql -h localhost -p 5432 -U appuser -d appdb -f deploy/sql/postgres-init.sql

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS document_chunks (
  id BIGINT PRIMARY KEY,
  document_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  embedding vector(1536),
  chunk_index INT NOT NULL,
  token_count INT,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_chunk_document_id ON document_chunks(document_id);
CREATE INDEX IF NOT EXISTS idx_chunk_embedding ON document_chunks USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
```

## ES 日志文档结构

索引：decision-logs-{yyyy.MM}（按月滚动）

DecisionLog 字段：
- logId（String, 唯一ID）
- traceId（String, 链路追踪ID，一次决策流程共享同一个 traceId）
- stationId（Long, 电站ID）
- deviceId、deviceType、equipSn
- sceneId、sceneName
- stage（枚举：DATA_RECEIVED / RULE_EVALUATED / SCENARIO_MATCHED / RAG_RETRIEVED / LLM_CALLED / DECISION_SAVED / TASK_EXECUTED / RESULT_PUBLISHED）
- stageStatus（SUCCESS / FAILED / SKIPPED）
- inputData（String, 该阶段输入摘要）
- outputData（String, 该阶段输出摘要）
- errorMessage
- costMillis（Long, 该阶段耗时）
- totalCostMillis（Long, 整体决策耗时）
- timestamp

查询接口：
- GET /api/v1/logs?stationId=xxx&deviceId=xxx&startTime=xxx&endTime=xxx
- GET /api/v1/logs/trace/{traceId}

## Kafka 设计

- 消费 topic：device-data，Consumer Group：ai-smart-group
- 生产 topic：decision-result
- DeviceDataMessage：{ stationId, deviceId, deviceType, equipSn, metrics(Map<String,Object> 包含 soc/power/temperature/statusCode 等), timestamp }
- DecisionResultMessage：{ decisionId, stationId, deviceId, equipSn, sceneName, suggestion, confidence, status, timestamp }
- 消费失败记 ES 日志 + ERROR 日志，不阻塞
- 注意：Kafka 宿主机端口是 19092

## 配置文件（必须与已有基础设施端口和密码一致）

smart-starter/src/main/resources/ 下：

application.yml：公共配置、激活 dev profile

application-dev.yml：
```yaml
# ============ 数据源 ============
# MySQL（ai_smart 库，需先执行 deploy/sql/mysql-init.sql 建库建表）
spring:
  datasource:
    mysql:
      jdbc-url: jdbc:mysql://localhost:3306/ai_smart?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
      username: root
      password: root123
      driver-class-name: com.mysql.cj.jdbc.Driver
    # PostgreSQL（已有 appdb 库，在其中建 pgvector 扩展和表）
    postgres:
      jdbc-url: jdbc:postgresql://localhost:5432/appdb
      username: appuser
      password: apppass
      driver-class-name: org.postgresql.Driver

  # ============ Redis（端口 16379，有密码）============
  data:
    redis:
      host: localhost
      port: 16379
      password: redis123

  # ============ Kafka（端口 19092）============
  kafka:
    bootstrap-servers: localhost:19092
    consumer:
      group-id: ai-smart-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

  # ============ Elasticsearch ============
  elasticsearch:
    uris: http://localhost:9200

  # ============ Spring AI ============
  ai:
    openai:
      api-key: ${AI_API_KEY:sk-placeholder}
      base-url: ${AI_BASE_URL:https://dashscope.aliyuncs.com/compatible-mode/v1}
      chat:
        options:
          model: ${AI_CHAT_MODEL:qwen-plus}
      embedding:
        options:
          model: ${AI_EMBEDDING_MODEL:text-embedding-v3}

# ============ MyBatis Plus ============
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: ASSIGN_ID
```

logback-spring.xml：日志格式包含 traceId，控制台 + 文件输出

.env.example：
```
AI_API_KEY=sk-your-api-key-here
AI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode/v1
AI_CHAT_MODEL=qwen-plus
AI_EMBEDDING_MODEL=text-embedding-v3
```

## 核心业务流程

### 流程一：电站和设备管理（基础 CRUD）
- POST/GET /api/v1/stations
- POST/GET /api/v1/devices
- GET /api/v1/stations/{id}/devices（查某电站下的设备）

### 流程二：场景管理（含条件和任务的级联操作）
- POST /api/v1/scenes（创建场景，同时传入 conditions 和 tasks）
- PUT /api/v1/scenes/{id}（更新后同步刷新 Redis 正向缓存和反向索引）
- GET /api/v1/scenes?stationId=xxx（查某电站的场景列表）
- GET /api/v1/scenes/{id}（场景详情，包含关联的 conditions 和 tasks）
- DELETE /api/v1/scenes/{id}（级联删除 + 清理 Redis 正向缓存和反向索引）

### 流程三：知识库管理
- POST /api/v1/knowledge/upload → 上传文档 → 分块 → embedding → 存 pgvector
- GET /api/v1/knowledge → 列表（分页）
- DELETE /api/v1/knowledge/{id}

### 流程四：设备数据驱动决策（核心亮点）
1. IoT 设备通过 Kafka topic（device-data）上报工作数据（含 stationId、equipSn）
2. DeviceDataConsumer 消费消息
3. 从 Redis 正向缓存 smart:equipScene:{equipSn} 获取该设备关联的所有场景（含 conditions）
4. RuleEngineService 遍历场景条件，判断设备数据是否满足触发规则
5. 命中规则的场景：
   - 若 ai_enabled=0 → 直接执行 task（规则模式），更新 Redis 执行状态 smart:executeStatus:{sceneId}:{equipSn}
   - 若 ai_enabled=1 → 走 RAG pipeline：
     a. 用 ai_query_template + 设备数据 + scene_desc 构建查询
     b. 向量检索知识库中相关的设备手册/故障案例
     c. 组装 prompt，调用 LLM 生成决策建议和置信度
     d. 置信度高于阈值 → 执行 AI 建议；低于阈值 → 回退到规则执行
6. 结果 → MySQL 持久化（smart_ai_decision）+ Redis 决策缓存 + Kafka（decision-result）发布 + ES 全链路日志

### 流程五：决策查询
- POST /api/v1/decisions/query（手动触发决策，传入设备数据）
- GET /api/v1/decisions/{id}（详情 + 溯源引用的知识片段）
- GET /api/v1/decisions?stationId=xxx（分页列表）

### 流程六：执行日志查询
- GET /api/v1/logs?stationId=xxx&deviceId=xxx&startTime=xxx&endTime=xxx
- GET /api/v1/logs/trace/{traceId}

## 代码规范：阿里巴巴 P3C

在父 pom 中集成 maven-pmd-plugin + p3c-pmd：

1. 父 pom 的 <build><plugins> 中添加 maven-pmd-plugin 3.21.2
2. 引入 com.alibaba.p3c:p3c-pmd:2.1.1 作为 plugin dependency
3. 配置 rulesets 引用所有阿里规范规则：
   - rulesets/java/ali-comment.xml
   - rulesets/java/ali-concurrent.xml
   - rulesets/java/ali-constant.xml
   - rulesets/java/ali-exception.xml
   - rulesets/java/ali-flowcontrol.xml
   - rulesets/java/ali-naming.xml
   - rulesets/java/ali-oop.xml
   - rulesets/java/ali-orm.xml
   - rulesets/java/ali-other.xml
   - rulesets/java/ali-set.xml
4. 绑定到 validate phase，mvn compile 时自动检查
5. 首次生成的代码必须通过 P3C 检查，不能有 violation

## 单元测试 + 覆盖率

1. 父 pom 统一管理 JUnit 5 + Mockito + AssertJ 版本

2. jacoco-maven-plugin：
   - 绑定 prepare-agent 和 report goal
   - smart-application 和 smart-infrastructure 模块覆盖率阈值 LINE 50%
   - mvn verify 时覆盖率不达标构建失败
   - 报告输出到 target/site/jacoco/

3. 生成以下单元测试类（对应模块的 src/test/java 下）：

   RuleEngineServiceTest：
   - @DisplayName("电池SOC超过阈值时触发场景")
   - @DisplayName("条件不满足时不触发场景")
   - @DisplayName("多个条件需全部满足才触发")

   ScenarioCacheServiceTest：
   - @DisplayName("缓存命中时直接返回场景配置")
   - @DisplayName("缓存未命中时从数据库加载并写入缓存")
   - @DisplayName("场景更新后刷新正向缓存和反向索引")
   - @DisplayName("执行状态独立存取不影响场景缓存")

   DecisionAppServiceTest：
   - @DisplayName("规则匹配且AI启用时走RAG决策流程")
   - @DisplayName("规则匹配但AI未启用时直接执行任务")
   - @DisplayName("AI置信度低于阈值时回退到规则执行")
   - @DisplayName("LLM调用失败时记录错误日志并返回失败状态")

   DeviceDataConsumerTest：
   - @DisplayName("正常消费设备数据并触发决策")
   - @DisplayName("消息格式异常时记录错误不阻塞")
   - @DisplayName("决策流程异常时记录ES日志")

   KnowledgeAppServiceTest：
   - @DisplayName("上传文档并完成分块和向量化")
   - @DisplayName("删除文档同时清理关联的分块数据")
   - @DisplayName("不支持的文件类型抛出业务异常")

   每个测试类使用 Mockito @Mock + @InjectMocks，覆盖正常/异常/边界三类场景。

## 代码完成度要求

✅ 完整实现的：
- 整个项目结构和所有 pom.xml（含 P3C 插件和 Jacoco 插件配置）
- 所有配置文件（application.yml、application-dev.yml、logback-spring.xml、.env.example）
- 初始化 SQL 脚本（deploy/sql/ 目录）
- 所有实体类（与数据库表完全对应）、所有 Mapper 接口
- 所有 DTO（request + response + cache DTO）
- SmartSceneDTO 缓存结构（含 AI 扩展字段，不含 executeStatus）
- RedisKeys 常量类（所有 key 格式和辅助方法）
- RedisConfig（hashKey 用 StringRedisSerializer，hashValue 用 Jackson）
- RedisService（封装 String/Hash/Set 三种操作）
- ScenarioCacheService（正向缓存 + 反向索引 + 执行状态，方法签名和核心逻辑完整）
- Controller 层（返回 mock 数据，Swagger 注解完整，包含接口描述和参数说明）
- 统一返回 Result<T> 封装 + 全局异常处理器（GlobalExceptionHandler）
- 所有枚举、常量类
- 多数据源配置（MysqlDataSourceConfig + PostgresDataSourceConfig）
- 所有单元测试骨架
- pgvector 的自定义 TypeHandler

⬜ 骨架 + TODO 注释的：
- RAG pipeline 各类（接口定义清晰，方法体写 TODO）
- RuleEngineService（方法签名和条件匹配逻辑框架写好，具体每种 conditionType 的判断写 TODO）
- Kafka Consumer（消费消息后打日志，不做实际业务处理，注释说明后续接入流程）
- Kafka Producer（方法体写 TODO）
- ES 日志服务（方法签名完整，方法体写 TODO）
- Application Service 中的 DecisionAppService（流程注释清晰，调用链用 TODO 标注）
- DecisionLogAspect（切面定义好，逻辑写 TODO）

⬜ 不需要实现的：
- 具体 RAG 检索逻辑
- 真实 LLM 调用
- 真实 embedding 计算

## 其他文件

### README.md（中文）

包含以下内容：

1. **项目简介**：一段话说明这是一个光伏储能领域的 AI 智能决策系统，在传统规则引擎基础上引入 RAG + LLM，实现从"人写规则"到"AI 辅助决策"的升级。

2. **整体架构图**（Mermaid 流程图），画出完整链路：
   ```
   设备上报数据 → Kafka(device-data) → Consumer
     → Redis 正向缓存加载设备场景配置 smart:equipScene:{equipSn}
     → 规则引擎匹配条件（SmartCondition）
     → [ai_enabled=0] 直接执行任务，更新 Redis 执行状态
     → [ai_enabled=1] RAG pipeline
       → pgvector 向量检索知识库
       → LLM 生成决策建议
       → 置信度判断（高→执行AI建议 / 低→回退规则）
     → MySQL 持久化决策记录（smart_ai_decision）
     → Redis 缓存决策结果
     → Kafka(decision-result) 发布
     → Elasticsearch 记录全链路日志
   ```

3. **存储职责说明**（表格）：

   | 存储 | 用途 | 选型理由 |
   |------|------|----------|
   | MySQL | 业务主库（电站、设备、场景、条件、任务、AI决策记录、知识文档元数据） | 事务支持、关系查询 |
   | PostgreSQL + pgvector | 文档分块向量存储 | 原生向量索引、高效相似度检索 |
   | Redis | 设备维度场景缓存（正向）、场景设备反向索引、执行状态、决策结果缓存 | 低延迟、减轻数据库压力 |
   | Elasticsearch | 决策执行全链路日志 | 全文检索、按时间范围聚合分析 |
   | Kafka | 设备数据接入、决策结果分发 | 解耦、削峰、支持重放 |

4. **Redis 缓存架构说明**（说明正向缓存、反向索引、执行状态三层设计的理由和 key 格式）

5. **技术栈清单**

6. **基础设施说明**：项目依赖已有的 Docker Compose 环境，列出各组件的宿主机端口和凭证

7. **快速启动步骤**：
   ```
   # 0. 确保 Docker Compose 基础设施已启动（MySQL/PG/Redis/Kafka/ES）
   # 1. 克隆项目
   git clone xxx
   cd ai-smart-demo
   # 2. 配置 AI API Key
   cp .env.example .env   # 编辑 .env 填入 API Key
   # 3. 初始化数据库
   make db-init
   # 4. 构建项目
   mvn clean install -DskipTests
   # 5. 启动应用
   make run
   # 6. 访问 Swagger
   open http://localhost:8080/swagger-ui.html
   # 7. 初始化示例数据
   curl -X POST http://localhost:8080/api/dev/simulate/init-data
   ```

8. **模块说明**（表格说明各模块职责）

9. **API 接口总览**（按模块列出所有接口路径和说明）

10. **开发规范**：
    - 代码遵循阿里巴巴 P3C 规范，提交前运行 make lint
    - 每次新增/修改业务类需同步更新单元测试
    - Claude CLI prompt 模板："请实现 XXX 功能，实现完成后：1）为新增/修改的类生成单元测试 2）运行 mvn pmd:check 确认无规范问题 3）运行 mvn test 确认测试通过，如果有问题自动修复"

11. **模型切换说明**：通过环境变量切换 OpenAI / 通义千问 / DeepSeek / Ollama

### .gitignore
Java / Maven / IDE / .env / target / logs / *.iml / .idea / .DS_Store

### Makefile
```makefile
.PHONY: run build test test-coverage lint clean db-init

# 初始化数据库（连接已有的 Docker 基础设施，创建库和表）
db-init:
	@echo "Initializing MySQL database..."
	mysql -h localhost -P 3306 -uroot -proot123 < deploy/sql/mysql-init.sql
	@echo "Initializing PostgreSQL tables and pgvector extension..."
	PGPASSWORD=apppass psql -h localhost -p 5432 -U appuser -d appdb -f deploy/sql/postgres-init.sql
	@echo "Database initialization complete."

run:
	cd smart-starter && mvn spring-boot:run -Dspring-boot.run.profiles=dev

build:
	mvn clean install -DskipTests

test:
	mvn test

test-coverage:
	mvn verify
	@echo "Jacoco report: smart-application/target/site/jacoco/index.html"

lint:
	mvn pmd:check

clean:
	mvn clean
```

### http/ 目录（IntelliJ HTTP Client 格式，所有请求使用正确的端口）
- station-api.http（电站增查）
- device-api.http（设备增查、按电站查设备）
- scene-api.http（场景增删改查，包含 conditions 和 tasks 的级联操作）
- knowledge-api.http（知识库上传、列表、删除）
- decision-api.http（手动触发决策、查详情、查列表）
- log-api.http（按条件查日志、按 traceId 查链路）
- simulate-api.http（模拟发送设备数据、初始化示例数据）

### SimulateController（@Profile("dev")）
- POST /api/dev/simulate/device-data：接收设备数据 JSON，发送到 Kafka device-data topic
- POST /api/dev/simulate/init-data：初始化示例数据，包括：
  - 1 个电站（示例光伏电站，含经纬度和装机容量）
  - 3 个设备（逆变器 INV-001、电池 BAT-001、电表 MTR-001）
  - 2 个智能场景：
    - 场景一（纯规则 ai_enabled=0）：电池SOC>90% 且 卖电电价>0.5 → 逆变器切换卖电模式
    - 场景二（AI辅助 ai_enabled=1）：设备状态码异常 → AI 诊断建议，ai_query_template 填好示例模板
  - 每个场景包含对应的 conditions 和 tasks
  - 数据写入 MySQL，同时预热到 Redis（正向缓存 + 反向索引）

## 最终验证

生成所有代码后，请依次执行以下命令确认一切正常：
1. mvn clean install -DskipTests → BUILD SUCCESS
2. mvn pmd:check → 无 violation
3. mvn test → 测试通过
4. 确保 application-dev.yml 中所有端口和密码与已有基础设施一致（MySQL:3306, PG:5432, Redis:16379+密码redis123, Kafka:19092, ES:9200）
