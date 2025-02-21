package com.cong.fishisland.controller.hot;

import com.cong.fishisland.common.BaseResponse;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.model.vo.hot.HotPostVO;
import com.cong.fishisland.service.HotPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 热榜数据接口
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@RestController
@RequestMapping("/hot")
@Slf4j
@RequiredArgsConstructor
//@Api(tags = "热榜数据")
public class HotPostController {

    private final HotPostService hotPostService;
    /**
     * 获取列表（封装类）
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取列表（封装类）")
    public BaseResponse<List<HotPostVO>> getHotPostList() {
        return ResultUtils.success(hotPostService.getHotPostList());
    }



}
