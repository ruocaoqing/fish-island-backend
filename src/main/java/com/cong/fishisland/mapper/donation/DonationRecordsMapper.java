package com.cong.fishisland.mapper.donation;

import com.cong.fishisland.model.entity.donation.DonationRecords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

/**
* @author Shing
* @description 针对表【donation_records(用户打赏记录表)】的数据库操作Mapper
* @createDate 2025-04-21 15:07:19
* @Entity com.cong.fishisland.model.entity.Donnation.DonationRecords
*/
public interface DonationRecordsMapper extends BaseMapper<DonationRecords> {

    /**
     * 根据 donorId 查询打赏记录，并加锁（悲观锁）
     */
    @Select("SELECT * FROM donation_records WHERE donorId = #{donorId} FOR UPDATE")
    DonationRecords selectByDonorIdForUpdate(@Param("donorId") Long donorId);

}




