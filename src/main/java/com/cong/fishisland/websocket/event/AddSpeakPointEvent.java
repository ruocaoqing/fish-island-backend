package com.cong.fishisland.websocket.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户聊天增加积分事件
 *
 * @author cong
 * @date 2024/02/18
 */
@Getter
public class AddSpeakPointEvent extends ApplicationEvent {
    private final String userId;

    public AddSpeakPointEvent(Object source, String userId) {
        super(source);
        this.userId = userId;
    }
}
