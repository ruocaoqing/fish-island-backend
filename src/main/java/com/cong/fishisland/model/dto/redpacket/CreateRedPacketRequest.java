package com.cong.fishisland.model.dto.redpacket;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 创建红包请求
 * @author cong
 */
@Data
//@ApiModel(value = "创建红包请求")
public class CreateRedPacketRequest {
    
    /**
     * 红包总金额（积分）
     */
    @ApiModelProperty(value = "红包总金额（积分）", required = true, example = "50")
    private Integer totalAmount;
    
    /**
     * 红包个数
     */
    @ApiModelProperty(value = "红包个数", required = true, example = "10")
    private Integer count;
    
    /**
     * 红包类型：1-随机红包，2-平均红包
     */
    @ApiModelProperty(value = "红包类型：1-随机红包，2-平均红包", required = true, example = "1")
    private Integer type;

    /**
     * 红包名称
     */
    private String name;
} 