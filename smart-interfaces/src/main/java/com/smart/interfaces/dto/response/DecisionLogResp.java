package com.smart.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "决策日志响应")
public class DecisionLogResp {
    @Schema(description = "日志ID")
    private String id;
    @Schema(description = "决策ID")
    private Long decisionId;
    @Schema(description = "阶段")
    private String stage;
    @Schema(description = "输入")
    private String input;
    @Schema(description = "输出")
    private String output;
    @Schema(description = "耗时(ms)")
    private Long costMs;
    @Schema(description = "是否成功")
    private Boolean success;
    @Schema(description = "错误信息")
    private String errorMsg;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
