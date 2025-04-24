package com.cong.fishisland.controller.redpacket;

import cn.dev33.satoken.stp.StpUtil;
import com.cong.fishisland.common.BaseResponse;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.model.dto.redpacket.CreateRedPacketRequest;
import com.cong.fishisland.model.entity.redpacket.RedPacket;
import com.cong.fishisland.model.vo.redpacket.RedPacketRecordVO;
import com.cong.fishisland.service.RedPacketService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 红包控制器
 * @author cong
 */
@RestController
@RequestMapping("/redpacket")
//@Api(value = "红包")
@Slf4j
@RequiredArgsConstructor
public class RedPacketController {

    private final RedPacketService redPacketService;

    @PostMapping("/create")
    @ApiOperation(value = "创建红包")
    public BaseResponse<String> createRedPacket(@RequestBody @Validated CreateRedPacketRequest request) {
        String redPacketId = redPacketService.createRedPacket(request);
        return ResultUtils.success(redPacketId);
    }

    @PostMapping("/grab")
    @ApiOperation(value = "抢红包")
    public BaseResponse<Integer> grabRedPacket(@RequestParam @ApiParam(value = "红包ID", required = true) String redPacketId) {
        Long userId = Long.valueOf(StpUtil.getLoginId().toString());
        Integer amount = redPacketService.grabRedPacket(redPacketId, userId);
        return ResultUtils.success(amount);
    }

    @GetMapping("/records")
    @ApiOperation(value = "获取红包抢购记录")
    public BaseResponse<List<RedPacketRecordVO>> getRedPacketRecords(@RequestParam @ApiParam(value = "红包ID", required = true) String redPacketId) {
        List<RedPacketRecordVO> records = redPacketService.getRedPacketRecords(redPacketId);
        return ResultUtils.success(records);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "获取红包详情")
    public BaseResponse<RedPacket> getRedPacketDetail(@RequestParam @ApiParam(value = "红包ID", required = true) String redPacketId) {
        RedPacket redPacketVO = redPacketService.getRedPacketDetail(redPacketId);
        return ResultUtils.success(redPacketVO);
    }

} 