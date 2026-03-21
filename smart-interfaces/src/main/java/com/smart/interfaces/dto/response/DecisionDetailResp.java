package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 决策详情响应 DTO
 *
 * <p>用于向前端返回一次决策的完整信息。决策记录由系统自动生成，
 * 记录了从设备数据触发到 AI 推理再到指令下发的完整决策过程。
 *
 * <p>决策状态枚举：
 * <ul>
 *   <li>0 - 待处理</li>
 *   <li>1 - 执行中</li>
 *   <li>2 - 已完成</li>
 *   <li>3 - 失败</li>
 * </ul>
 *
 * @author Joseph Ho
 */
@Data
@Schema(description = "决策详情响应")
public class DecisionDetailResp {
    /** 决策记录主键 ID */
    @Schema(description = "决策ID")
    private Long id;

    /** 关联的电站 ID，标识决策发生在哪个电站 */
    @Schema(description = "电站ID")
    private Long stationId;

    /** 关联的场景 ID，标识决策由哪个场景的规则触发 */
    @Schema(description = "场景ID")
    private Long sceneId;

    /** 触发数据，JSON 格式，记录触发决策的原始设备数据 */
    @Schema(description = "触发数据")
    private String triggerData;

    /** 决策结果，JSON 格式，记录最终生成的控制指令 */
    @Schema(description = "决策结果")
    private String decisionResult;

    /** 决策状态：0-待处理 1-执行中 2-已完成 3-失败 */
    @Schema(description = "状态")
    private Integer status;

    /** 决策执行阶段，标识当前执行到哪个步骤（如规则匹配、AI推理、指令下发） */
    @Schema(description = "阶段")
    private Integer stage;

    /** AI 大模型的原始响应内容，用于追溯 AI 的推理过程 */
    @Schema(description = "AI响应")
    private String aiResponse;

    /** 决策执行完成的时间 */
    @Schema(description = "执行时间")
    private LocalDateTime executedAt;

    /** 决策记录创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
