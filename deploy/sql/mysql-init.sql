-- AI Smart Demo MySQL 初始化脚本
-- 数据库: appdb

CREATE DATABASE IF NOT EXISTS appdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE appdb;

-- 电站表
CREATE TABLE IF NOT EXISTS t_station (
    id BIGINT NOT NULL COMMENT '主键',
    station_name VARCHAR(100) NOT NULL COMMENT '电站名称',
    location VARCHAR(255) DEFAULT NULL COMMENT '电站位置',
    capacity DECIMAL(12,2) DEFAULT NULL COMMENT '装机容量(kW)',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-离线 1-在线 2-告警',
    contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电站表';

-- 设备表
CREATE TABLE IF NOT EXISTS t_device (
    id BIGINT NOT NULL COMMENT '主键',
    station_id BIGINT NOT NULL COMMENT '电站ID',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_type TINYINT NOT NULL COMMENT '设备类型: 1-光伏板 2-储能电池 3-逆变器 4-电表',
    device_sn VARCHAR(100) DEFAULT NULL COMMENT '设备序列号',
    rated_power DECIMAL(12,2) DEFAULT NULL COMMENT '额定功率(kW)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-离线 1-在线 2-告警',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_station_id (station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 智能场景表
CREATE TABLE IF NOT EXISTS t_smart_scene (
    id BIGINT NOT NULL COMMENT '主键',
    scene_name VARCHAR(100) NOT NULL COMMENT '场景名称',
    scenario_type TINYINT NOT NULL COMMENT '场景类型: 1-削峰填谷 2-需量控制 3-光伏自消纳 4-应急备电',
    station_id BIGINT NOT NULL COMMENT '电站ID',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    priority INT DEFAULT 0 COMMENT '优先级',
    description VARCHAR(500) DEFAULT NULL COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_station_id (station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能场景表';

-- 场景规则表
CREATE TABLE IF NOT EXISTS t_scene_rule (
    id BIGINT NOT NULL COMMENT '主键',
    scene_id BIGINT NOT NULL COMMENT '场景ID',
    condition_type TINYINT NOT NULL COMMENT '条件类型: 1-SOC 2-功率 3-电压 4-温度 5-时间段',
    condition_sign TINYINT NOT NULL COMMENT '比较符: 1-> 2->= 3-< 4-<= 5-== 6-between',
    threshold_value VARCHAR(100) NOT NULL COMMENT '阈值',
    action VARCHAR(500) DEFAULT NULL COMMENT '动作指令',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_scene_id (scene_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景规则表';

-- 设备数据表
CREATE TABLE IF NOT EXISTS t_device_data (
    id BIGINT NOT NULL COMMENT '主键',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    station_id BIGINT NOT NULL COMMENT '电站ID',
    power DECIMAL(12,2) DEFAULT NULL COMMENT '功率(kW)',
    voltage DECIMAL(8,2) DEFAULT NULL COMMENT '电压(V)',
    current DECIMAL(8,2) DEFAULT NULL COMMENT '电流(A)',
    temperature DECIMAL(6,2) DEFAULT NULL COMMENT '温度(℃)',
    soc DECIMAL(5,2) DEFAULT NULL COMMENT 'SOC(%)',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_device_id (device_id),
    INDEX idx_station_id (station_id),
    INDEX idx_collect_time (collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备数据表';

-- 决策记录表
CREATE TABLE IF NOT EXISTS t_decision_record (
    id BIGINT NOT NULL COMMENT '主键',
    station_id BIGINT NOT NULL COMMENT '电站ID',
    scene_id BIGINT DEFAULT NULL COMMENT '场景ID',
    trigger_data TEXT DEFAULT NULL COMMENT '触发数据(JSON)',
    decision_result TEXT DEFAULT NULL COMMENT '决策结果',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待处理 1-执行中 2-已完成 3-失败',
    stage TINYINT DEFAULT 1 COMMENT '阶段: 1-规则匹配 2-RAG检索 3-AI推理 4-指令下发',
    ai_response TEXT DEFAULT NULL COMMENT 'AI响应',
    executed_at DATETIME DEFAULT NULL COMMENT '执行时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_station_id (station_id),
    INDEX idx_scene_id (scene_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='决策记录表';

-- 知识文档表
CREATE TABLE IF NOT EXISTS t_knowledge_doc (
    id BIGINT NOT NULL COMMENT '主键',
    doc_name VARCHAR(200) NOT NULL COMMENT '文档名称',
    doc_type VARCHAR(20) DEFAULT NULL COMMENT '文档类型',
    file_path VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
    chunk_count INT DEFAULT 0 COMMENT '分片数量',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-处理中 1-已完成 2-失败',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识文档表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS t_operation_log (
    id BIGINT NOT NULL COMMENT '主键',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operator_name VARCHAR(50) DEFAULT NULL COMMENT '操作人',
    target_type VARCHAR(50) DEFAULT NULL COMMENT '目标类型',
    target_id BIGINT DEFAULT NULL COMMENT '目标ID',
    detail TEXT DEFAULT NULL COMMENT '详情',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
