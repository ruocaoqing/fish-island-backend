package com.cong.fishisland.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.fishisland.model.dto.donation.DonationRecordsAddRequest;
import com.cong.fishisland.model.dto.donation.DonationRecordsQueryRequest;
import com.cong.fishisland.model.entity.donation.DonationRecords;
import com.cong.fishisland.model.entity.post.Post;
import com.cong.fishisland.model.vo.donation.DonationRecordsVO;

/**
 * @author Shing
 * @description 针对表【donation_records(用户打赏记录表)】的数据库操作Service
 * @createDate 2025-04-21 15:07:19
 */
public interface DonationRecordsService extends IService<DonationRecords> {

    /**
     * 获取查询条件
     *
     * @param donationRecordsQueryRequest 打赏查询请求
     * @return {@link QueryWrapper}<{@link Post}>
     */
    QueryWrapper<DonationRecords> getQueryWrapper(DonationRecordsQueryRequest donationRecordsQueryRequest);

    // region 增删改查

    /**
     * 新增打赏记录
     *
     * @param donationRecordsAddRequest 打赏添加请求
     * @return 新增记录的主键 ID
     */
    Long createRecord(DonationRecordsAddRequest donationRecordsAddRequest);

    /**
     * 获取打赏记录封装
     *
     * @param donationRecords 打赏记录
     * @return {@link DonationRecordsVO}
     */
    DonationRecordsVO getRecordVO(DonationRecords donationRecords);


    /**
     * 分页查询打赏记录
     *
     * @param donationRecordsPage 打赏记录分页
     * @return {@link Page}<{@link DonationRecordsVO}>
     */
    Page<DonationRecordsVO> getRecordsVOPage(Page<DonationRecords> donationRecordsPage);

    // endregion

}
