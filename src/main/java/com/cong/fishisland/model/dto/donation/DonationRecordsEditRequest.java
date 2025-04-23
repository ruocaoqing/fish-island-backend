package com.cong.fishisland.model.dto.donation;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 编辑请求
 *
 * @author shing
 */
@Data
public class DonationRecordsEditRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}