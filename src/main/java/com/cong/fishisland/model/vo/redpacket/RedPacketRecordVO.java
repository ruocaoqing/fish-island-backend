package com.cong.fishisland.model.vo.redpacket;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 红包记录VO
 * @author cong
 */
@Data
@ApiModel(value = "红包记录VO")
public class RedPacketRecordVO {
    
    /**
     * 记录ID
     */
    @ApiModelProperty(value = "记录ID", example = "rpr123456789")
    private String id;
    
    /**
     * 红包ID
     */
    @ApiModelProperty(value = "红包ID", example = "rp123456789")
    private String redPacketId;
    
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "123")
    private Long userId;
    
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", example = "张三")
    private String userName;
    
    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像", example = "http://example.com/avatar.jpg")
    private String userAvatar;

    
    /**
     * 抢到的金额
     */
    @ApiModelProperty(value = "抢到的金额", example = "5")
    private Integer amount;
    
    /**
     * 抢红包时间
     */
    @ApiModelProperty(value = "抢红包时间")
    private Date grabTime;
} 