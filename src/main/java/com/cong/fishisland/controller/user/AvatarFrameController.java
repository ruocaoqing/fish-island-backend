package com.cong.fishisland.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.fishisland.common.BaseResponse;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.common.exception.ThrowUtils;
import com.cong.fishisland.model.dto.user.AvatarFrameQueryRequest;
import com.cong.fishisland.model.entity.user.AvatarFrame;
import com.cong.fishisland.model.vo.user.AvatarFrameVO;
import com.cong.fishisland.service.AvatarFrameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 头像框接口
 *
 * @author cong
 */
@RestController
@RequestMapping("/api/avatar/frame")
@RequiredArgsConstructor
//@Api(tags = "头像框接口")
public class AvatarFrameController {

    private final AvatarFrameService avatarFrameService;

    /**
     * 获取用户可用的头像框列表
     *
     * @return {@link BaseResponse}<{@link List}<{@link AvatarFrame}>>
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取用户可用的头像框列表")
    public BaseResponse<List<AvatarFrame>> listAvailableFrames() {
        return ResultUtils.success(avatarFrameService.listAvailableFrames());
    }

    /**
     * 兑换头像框
     *
     * @param frameId 头像框ID
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/exchange")
    @ApiOperation(value = "兑换头像框")
    public BaseResponse<Boolean> exchangeFrame(@RequestParam Long frameId) {
        if (frameId == null || frameId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(avatarFrameService.exchangeFrame(frameId));
    }

    /**
     * 设置当前使用的头像框
     *
     * @param frameId 头像框ID
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/set")
    @ApiOperation(value = "设置当前使用的头像框")
    public BaseResponse<Boolean> setCurrentFrame(@RequestParam Long frameId) {
        if (frameId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(avatarFrameService.setCurrentFrame(frameId));
    }

    /**
     * 分页获取头像框列表（封装类）
     *
     * @param avatarFrameQueryRequest 头像框查询请求
     * @return {@link BaseResponse}<{@link Page}<{@link AvatarFrameVO}>>
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取头像框列表（封装类）")
    public BaseResponse<Page<AvatarFrameVO>> listAvatarFrameVoByPage(@RequestBody AvatarFrameQueryRequest avatarFrameQueryRequest) {
        if (avatarFrameQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = avatarFrameQueryRequest.getCurrent();
        long size = avatarFrameQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<AvatarFrame> avatarFramePage = avatarFrameService.page(new Page<>(current, size),
                avatarFrameService.getQueryWrapper(avatarFrameQueryRequest));
        return ResultUtils.success(avatarFrameService.getAvatarFrameVOPage(avatarFramePage));
    }
} 