package com.cong.fishisland.websocket.listener;


import com.cong.fishisland.service.UserPointsService;
import com.cong.fishisland.websocket.event.AddSpeakPointEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户聊天积分监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AddSpeakPointListener {
    private final UserPointsService userPointsService;

    @Async
    @EventListener(classes = AddSpeakPointEvent.class)
    public void addSpeakPoint(AddSpeakPointEvent event) {
        String userId = event.getUserId();
        userPointsService.addSpeakPoint(Long.valueOf(userId));
        log.info("用户{}增加聊天积分成功", userId);
    }

}
