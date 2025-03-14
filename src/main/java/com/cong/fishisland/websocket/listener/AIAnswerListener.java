package com.cong.fishisland.websocket.listener;


import com.alibaba.fastjson.JSON;
import com.cong.fishisland.datasource.ai.AIChatDataSource;
import com.cong.fishisland.model.entity.chat.RoomMessage;
import com.cong.fishisland.model.enums.MessageTypeEnum;
import com.cong.fishisland.model.vo.ai.AiResponse;
import com.cong.fishisland.model.ws.request.Message;
import com.cong.fishisland.model.ws.request.MessageWrapper;
import com.cong.fishisland.model.ws.request.Sender;
import com.cong.fishisland.model.ws.response.WSBaseResp;
import com.cong.fishisland.service.RoomMessageService;
import com.cong.fishisland.websocket.event.AIAnswerEvent;
import com.cong.fishisland.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 机器人回答消息监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIAnswerListener {
    private final WebSocketService webSocketService;
    private final AIChatDataSource aiChatDataSource;
    private final RoomMessageService roomMessageService;

    @Async
    @EventListener(classes = AIAnswerEvent.class)
    public void sendAnswer(AIAnswerEvent event) {
        MessageWrapper messageDto = event.getMessageDto();
        Message message = messageDto.getMessage();
        AiResponse aiResponse = aiChatDataSource.getAiResponse(message.getContent());
        //AI 回答
        MessageWrapper messageWrapper = getMessageWrapper(aiResponse, message);

        webSocketService.sendToAllOnline(WSBaseResp.builder()
                .type(MessageTypeEnum.CHAT.getType())
                .data(messageWrapper).build());
        //保存消息到数据库
        RoomMessage roomMessage = new RoomMessage();
        roomMessage.setUserId(-1L);
        roomMessage.setRoomId(-1L);
        roomMessage.setMessageJson(JSON.toJSONString(messageWrapper));
        roomMessage.setMessageId(messageWrapper.getMessage().getId());
        roomMessageService.save(roomMessage);

    }

    private static @NotNull MessageWrapper getMessageWrapper(AiResponse aiResponse, Message message) {
        Message aiMessage = new Message();
        aiMessage.setContent(aiResponse.getAnswer());
        aiMessage.setId(String.valueOf(System.currentTimeMillis()));
        Sender aiSender = Sender.builder()
                .id("-1")
                .level(1)
                .name("摸鱼助手")
                .isAdmin(false)
                .points(9999)
                .avatar("https://s1.aigei.com/src/img/gif/3d/3dbb70bf3c81407cb5aaba07c79b317b.gif?imageMogr2/auto-orient/thumbnail/!282x270r/gravity/Center/crop/282x270/quality/85/%7CimageView2/2/w/282&e=2051020800&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:_J_OaEEsWRM6CkjjOHEHug85N7U=")
                .build();
        aiMessage.setSender(aiSender);
        aiMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));
        aiMessage.setQuotedMessage(message);
        aiMessage.setMentionedUsers(Collections.singletonList(message.getSender()));

        MessageWrapper messageWrapper = new MessageWrapper();
        messageWrapper.setMessage(aiMessage);
        return messageWrapper;
    }

}
