package com.cong.fishisland.model.vo.donation;

import com.cong.fishisland.model.entity.donation.DonationRecords;
import com.cong.fishisland.model.vo.user.LoginUserVO;
import com.cong.fishisland.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 打赏记录
 *
 * @author Shing
 * date 22/4/2025
 */
@Data
public class DonationRecordsVO implements Serializable {

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
     * 关联的捐赠用户信息
     */
    private LoginUserVO donorUser;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;


    /**
     * 对象转包装类
     *
     * @param donationRecords
     * @return
     */
    public static DonationRecordsVO objToVo(DonationRecords donationRecords) {
        if (donationRecords == null) {
            return null;
        }
        DonationRecordsVO donationRecordsVO = new DonationRecordsVO();
        BeanUtils.copyProperties(donationRecords, donationRecordsVO);
        return donationRecordsVO;
    }
}
