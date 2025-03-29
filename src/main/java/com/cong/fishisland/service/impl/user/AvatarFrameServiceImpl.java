package com.cong.fishisland.service.impl.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.constant.CommonConstant;
import com.cong.fishisland.model.dto.user.AvatarFrameQueryRequest;
import com.cong.fishisland.model.entity.user.AvatarFrame;
import com.cong.fishisland.model.entity.user.User;
import com.cong.fishisland.model.entity.user.UserPoints;
import com.cong.fishisland.model.vo.user.AvatarFrameVO;
import com.cong.fishisland.service.AvatarFrameService;
import com.cong.fishisland.mapper.user.AvatarFrameMapper;
import com.cong.fishisland.service.UserPointsService;
import com.cong.fishisland.service.UserService;
import com.cong.fishisland.utils.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author cong
 * @description 针对表【avatar_frame(头像框)】的数据库操作Service实现
 * @createDate 2025-03-26 19:41:34
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AvatarFrameServiceImpl extends ServiceImpl<AvatarFrameMapper, AvatarFrame>
        implements AvatarFrameService {
    private final UserService userService;
    private final UserPointsService userPointsService;

    @Override
    public List<AvatarFrame> listAvailableFrames() {
        // 1. 获取当前登录用户ID
        User loginUser = userService.getLoginUser();
        //2. 查询用户已拥有的头像框
        List<String> frameIds = Optional.ofNullable(JSON.parseArray(loginUser.getAvatarFramerList(), String.class))
                .orElse(new ArrayList<>());
        if (CollectionUtils.isEmpty(frameIds)) {
            return new ArrayList<>();
        }
        // 3. 返回头像框列表
        return this.listByIds(frameIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean exchangeFrame(Long frameId) {
        // 1. 检查头像框是否存在
        User loginUser = userService.getLoginUser();
        AvatarFrame frame = this.getById(frameId);
        if (frame == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "头像框不存在");
        }

        // 2. 检查用户是否已拥有该头像框
        //2. 查询用户已拥有的头像框
        List<String> frameIds = Optional.ofNullable(JSON.parseArray(loginUser.getAvatarFramerList(), String.class))
                .orElse(new ArrayList<>());
        if (frameIds.contains(frameId.toString())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已拥有该头像框");
        }
        // 3. 检查用户积分是否足够
        UserPoints userPoints = userPointsService.getById(loginUser.getId());
        int availablePoints = userPoints.getPoints() - userPoints.getUsedPoints();
        if (availablePoints < frame.getPoints()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户积分不足");
        }
        // 4. 扣除用户积分
        userPoints.setUsedPoints(userPoints.getUsedPoints() + frame.getPoints());
        userPointsService.updateById(userPoints);
        // 5. 添加头像框到用户背包
        frameIds.add(frameId.toString());
        loginUser.setAvatarFramerList(JSON.toJSONString(frameIds));
        userService.updateById(loginUser);
        // 6. 返回成功
        return true;
    }

    @Override
    public Boolean setCurrentFrame(Long frameId) {
        //如果为 -1 则清除当前头像框
        if (frameId == -1) {
            User loginUser = userService.getLoginUser();
            loginUser.setAvatarFramerUrl("");
            userService.updateById(loginUser);
            return true;
        }
        // 1. 检查头像框是否存在
        AvatarFrame frame = this.getById(frameId);
        if (frame == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "头像框不存在");
        }

        // 2. 检查用户是否拥有该头像框
        User loginUser = userService.getLoginUser();
        List<String> frameIds = JSON.parseArray(loginUser.getAvatarFramerList(), String.class);
        if (!frameIds.contains(frameId.toString())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未拥有该头像框");
        }
        // 3. 更新用户当前使用的头像框
        loginUser.setAvatarFramerUrl(frame.getUrl());
        userService.updateById(loginUser);
        // 4. 返回成功
        return true;
    }

    @Override
    public QueryWrapper<AvatarFrame> getQueryWrapper(AvatarFrameQueryRequest avatarFrameQueryRequest) {
        QueryWrapper<AvatarFrame> queryWrapper = new QueryWrapper<>();
        if (avatarFrameQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = avatarFrameQueryRequest.getSearchText();
        String sortField = avatarFrameQueryRequest.getSortField();
        String sortOrder = avatarFrameQueryRequest.getSortOrder();
        Long id = avatarFrameQueryRequest.getId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText));
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public AvatarFrameVO getAvatarFrameVO(AvatarFrame avatarFrame) {
        if (avatarFrame == null) {
            return null;
        }
        AvatarFrameVO avatarFrameVO = AvatarFrameVO.objToVo(avatarFrame);
        // 检查当前用户是否拥有该头像框
        User loginUser = userService.getLoginUser();
        List<String> frameIds = Optional.ofNullable(JSON.parseArray(loginUser.getAvatarFramerList(), String.class))
                .orElse(new ArrayList<>());
        avatarFrameVO.setHasOwned(frameIds.contains(Optional.ofNullable(avatarFrame.getFrameId())
                .orElse(0L).toString()));
        return avatarFrameVO;
    }

    @Override
    public Page<AvatarFrameVO> getAvatarFrameVOPage(Page<AvatarFrame> avatarFramePage) {
        List<AvatarFrame> avatarFrameList = avatarFramePage.getRecords();
        Page<AvatarFrameVO> avatarFrameVoPage = new Page<>(avatarFramePage.getCurrent(), avatarFramePage.getSize(), avatarFramePage.getTotal());
        if (avatarFrameList == null || avatarFrameList.isEmpty()) {
            return avatarFrameVoPage;
        }
        // 对象列表 => 封装对象列表
        List<AvatarFrameVO> avatarFrameVOList = avatarFrameList.stream()
                .map(this::getAvatarFrameVO).collect(Collectors.toList());
        avatarFrameVoPage.setRecords(avatarFrameVOList);
        return avatarFrameVoPage;
    }
}




