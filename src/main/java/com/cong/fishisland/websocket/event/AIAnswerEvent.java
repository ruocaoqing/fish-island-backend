package com.cong.fishisland.websocket.event;

import com.cong.fishisland.model.ws.request.MessageWrapper;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * AI  回答事件
 *
 * @author cong
 * @date 2024/02/18
 */
@Getter
public class AIAnswerEvent extends ApplicationEvent {
    private final MessageWrapper messageDto;

    public AIAnswerEvent(Object source, MessageWrapper messageDto) {
        super(source);
        this.messageDto = messageDto;
    }
}
