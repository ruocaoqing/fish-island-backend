package com.cong.fishisland.model.dto.donation;

import com.cong.fishisland.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询请求
 *
 * @author Shing
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DonationRecordsQueryRequest extends PageRequest implements Serializable {

    /**
     * 打赏记录ID
     */
    private Long id;

    /**
     * 打赏用户ID
     */
    private Long userId;

    /**
     * 打赏金额（精度：分）
     */
    private BigDecimal amount;

    /**
     * 转账说明/备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}