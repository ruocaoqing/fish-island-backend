package com.cong.fishisland.service.impl.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.constant.UserConstant;
import com.cong.fishisland.model.entity.user.User;
import com.cong.fishisland.model.entity.user.UserTitle;
import com.cong.fishisland.service.UserService;
import com.cong.fishisland.service.UserTitleService;
import com.cong.fishisland.mapper.user.UserTitleMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cong
 * @description 针对表【user_title(用户称号)】的数据库操作Service实现
 * @createDate 2025-04-30 10:07:06
 */
@Service
@RequiredArgsConstructor
public class UserTitleServiceImpl extends ServiceImpl<UserTitleMapper, UserTitle>
        implements UserTitleService {
    private final UserService userService;

    @Override
    public List<UserTitle> listAvailableTitles() {
        // 1. 获取当前登录用户ID
        User loginUser = userService.getLoginUser();
        //2. 查询用户已拥有的称号
        List<String> titleIds = Optional.ofNullable(JSON.parseArray(loginUser.getTitleIdList(), String.class))
                .orElse(new ArrayList<>());

        if (CollectionUtils.isEmpty(titleIds)) {
            return new ArrayList<>();
        }
        // 3. 返回称号列表
        return this.listByIds(titleIds);
    }

    @Override
    public Boolean setCurrentTitle(Long titleId) {
        User loginUser = userService.getLoginUser();

        Boolean result = checkSpecialDeal(titleId, loginUser);
        if (Boolean.TRUE.equals(result)) {
            return true;
        }

        // 1. 检查称号是否存在
        UserTitle userTitle = this.getById(titleId);
        if (userTitle == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "称号不存在");
        }

        // 2. 检查用户是否拥有该称号
        List<String> titleIds = JSON.parseArray(loginUser.getTitleIdList(), String.class);
        if (!titleIds.contains(titleId.toString())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未拥有该称号");
        }
        // 3. 更新用户当前使用的称号
        loginUser.setTitleId(userTitle.getTitleId());
        userService.updateById(loginUser);
        // 4. 返回成功
        return true;
    }

    @NotNull
    private Boolean checkSpecialDeal(Long titleId, User loginUser) {
        if (titleId == 0) {
            // 3. 更新用户当前使用的称号
            loginUser.setTitleId(titleId);
            userService.updateById(loginUser);
            return true;
        }
        if (titleId == -1L) {
            //检查是否是管理员
            if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "非管理员不能使用该称号");
            }
            // 3. 更新用户当前使用的称号
            loginUser.setTitleId(titleId);
            userService.updateById(loginUser);
            return true;
        }
        return false;
    }
}




