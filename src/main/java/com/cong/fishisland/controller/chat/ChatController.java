package com.cong.fishisland.controller.chat;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.fishisland.common.BaseResponse;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.model.dto.chat.MessageQueryRequest;
import com.cong.fishisland.model.vo.chat.RoomMessageVo;
import com.cong.fishisland.service.RoomMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天控制器
 *
 * @author cong
 * @date 2024/02/19
 */
@RestController
@RequestMapping("/chat")
@Slf4j
@RequiredArgsConstructor
@Api(value = "聊天")
public class ChatController {

    private final RoomMessageService roomMessageService;

    @PostMapping("/message/page/vo")
    @ApiOperation(value = "分页获取用户房间消息列表")
    public BaseResponse<Page<RoomMessageVo>> listMessageVoByPage(@RequestBody MessageQueryRequest messageQueryRequest) {
        Page<RoomMessageVo> messageVoPage = roomMessageService.listMessageVoByPage(messageQueryRequest);
        return ResultUtils.success(messageVoPage);
    }
}