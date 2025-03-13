package com.cong.fishisland.model.vo.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 响应
 *
 * @author cong
 * @date 2025/03/12
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AiResponse {

    @ApiModelProperty(value = "AI 回答记录ID")
    private String id;

    @ApiModelProperty(value = "AI 回答")
    private String answer;

    @ApiModelProperty(value = "模型名称")
    private String aiName;
}
