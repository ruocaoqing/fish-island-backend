package com.cong.fishisland.model.entity.donation;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户打赏记录表
 *
 * @TableName donation_records
 */
@TableName(value = "donation_records")
@Data
public class DonationRecords implements Serializable {

    /**
     * 打赏记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 打赏用户ID
     */
    private Long donorId;

    /**
     * 打赏金额（精度：分）
     */
    private BigDecimal amount;

    /**
     * 转账说明/备注
     */
    private String remark;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}