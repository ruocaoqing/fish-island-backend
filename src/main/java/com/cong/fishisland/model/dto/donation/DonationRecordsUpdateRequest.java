package com.cong.fishisland.model.dto.donation;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 更新请求
 *
 * @author Shing
 */
@Data
public class DonationRecordsUpdateRequest implements Serializable {

    /**
     * id
     */
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

    private static final long serialVersionUID = 1L;
}