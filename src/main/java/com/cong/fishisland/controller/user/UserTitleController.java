package com.cong.fishisland.controller.user;

import com.cong.fishisland.common.BaseResponse;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.model.entity.user.UserTitle;
import com.cong.fishisland.service.UserTitleService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 称号接口
 *
 * @author cong
 */
@RestController
@RequestMapping("/user/title")
@RequiredArgsConstructor
//@Api(tags = "称号接口")
public class UserTitleController {

    private final UserTitleService userTitleService;

    /**
     * 获取用户可用的称号列表
     *
     * @return {@link BaseResponse}<{@link List}<{@link UserTitle}>>
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取用户可用的称号列表")
    public BaseResponse<List<UserTitle>> listAvailableFrames() {
        return ResultUtils.success(userTitleService.listAvailableTitles());
    }


    /**
     * 设置当前使用的称号
     *
     * @param titleId 称号ID
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/set")
    @ApiOperation(value = "设置当前使用的称号")
    public BaseResponse<Boolean> setCurrentFrame(@RequestParam Long titleId) {
        if (titleId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userTitleService.setCurrentTitle(titleId));
    }

}