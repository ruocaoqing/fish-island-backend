package com.cong.fishisland.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.fishisland.model.dto.user.AvatarFrameQueryRequest;
import com.cong.fishisland.model.entity.user.AvatarFrame;
import com.cong.fishisland.model.vo.user.AvatarFrameVO;

import java.util.List;

/**
* @author cong
* @description 针对表【avatar_frame(头像框)】的数据库操作Service
* @createDate 2025-03-26 19:41:34
*/
public interface AvatarFrameService extends IService<AvatarFrame> {

    /**
     * 获取用户可用的头像框列表
     * @return 头像框列表
     */
    List<AvatarFrame> listAvailableFrames();

    /**
     * 兑换头像框
     * @param frameId 头像框ID
     * @return 是否兑换成功
     */
    Boolean exchangeFrame(Long frameId);

    /**
     * 设置当前使用的头像框
     * @param frameId 头像框ID
     * @return 是否设置成功
     */
    Boolean setCurrentFrame(Long frameId);

    /**
     * 获取查询条件
     *
     * @param avatarFrameQueryRequest 头像框查询请求
     * @return {@link QueryWrapper}<{@link AvatarFrame}>
     */
    QueryWrapper<AvatarFrame> getQueryWrapper(AvatarFrameQueryRequest avatarFrameQueryRequest);

    /**
     * 获取头像框封装
     *
     * @param avatarFrame 头像框
     * @return {@link AvatarFrameVO}
     */
    AvatarFrameVO getAvatarFrameVO(AvatarFrame avatarFrame);

    /**
     * 分页获取头像框封装
     *
     * @param avatarFramePage 头像框页面
     * @return {@link Page}<{@link AvatarFrameVO}>
     */
    Page<AvatarFrameVO> getAvatarFrameVOPage(Page<AvatarFrame> avatarFramePage);
}
