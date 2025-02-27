package com.cong.fishisland.websocket.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;

import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.config.ThreadPoolConfig;
import com.cong.fishisland.model.dto.ws.WSChannelExtraDTO;
import com.cong.fishisland.model.entity.User;
import com.cong.fishisland.model.enums.MessageTypeEnum;
import com.cong.fishisland.model.vo.ws.ChatMessageVo;
import com.cong.fishisland.model.ws.request.WSBaseReq;
import com.cong.fishisland.model.ws.response.WSBaseResp;
import com.cong.fishisland.service.UserService;
import com.cong.fishisland.websocket.cache.UserCache;
import com.cong.fishisland.websocket.event.UserOfflineEvent;
import com.cong.fishisland.websocket.event.UserOnlineEvent;
import com.cong.fishisland.websocket.service.WebSocketService;
import com.cong.fishisland.websocket.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Description: websocket处理类
 * Date: 2023-03-19 16:21
 *
 * @author liuhuaicong
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {
    /**
     * 所需服务
     */
    private final UserService userService;
    private final UserCache userCache;
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final ApplicationEventPublisher applicationEventPublisher;


    /**
     * 所有已连接的websocket连接列表和一些额外参数
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    /**
     * 所有单人在线的棋局和对应的socket
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<Channel>> CHESS_ROOM_MAP = new ConcurrentHashMap<>();

    /**
     * 所有双人加入棋局和对应的socket
     */
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<Channel>> CHESS_ROOM_PLAYER_MAP = new ConcurrentHashMap<>();

    @Override
    public void handleLoginReq(Channel channel) {
        try {
            String token = NettyUtil.getAttr(channel, NettyUtil.TOKEN);
            //更新上线列表
            online(channel, Long.valueOf(StpUtil.getLoginIdByToken(token).toString()));
            User loginUser = userService.getLoginUser(token);
            //发送用户上线事件
            boolean online = userCache.isOnline(loginUser.getId());
            if (!online) {
                loginUser.setUpdateTime(new Date());
                applicationEventPublisher.publishEvent(new UserOnlineEvent(this, loginUser));
            }
        } catch (Exception e) {
            log.error("websocket登录失败", e);
            channel.close();
        }

    }

    /**
     * 处理所有ws连接的事件
     *
     * @param channel 渠道
     */
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @Override
    public void removed(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offlineAll = offline(channel, uidOptional);
        if (uidOptional.isPresent() && offlineAll) {
            //已登录用户断连,并且全下线成功
            User user = new User();
            user.setId(uidOptional.get());
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    /**
     * 在线发送给所有人
     *
     * @param wsBaseResp WS基础研究
     * @param skipUid    跳过 UID
     */
    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (ObjectUtil.equal(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {
        sendToAllOnline(wsBaseResp, null);
    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp)));
    }

    @Override
    public void sendMessage(Channel channel, WSBaseReq req) {
        // 发送数据
        String content = req.getData();
        ChatMessageVo chatMessageVo = JSONUtil.toBean(content, ChatMessageVo.class);
        // 接收消息 用户id
        Long uid = req.getUserId();
        String token = NettyUtil.getAttr(channel, NettyUtil.TOKEN);
        if (CharSequenceUtil.isEmpty(token)) {
            // 异常返回
            WSBaseResp<Object> errorResp = WSBaseResp.builder().type(MessageTypeEnum.ERROR.getType()).data(ErrorCode.FORBIDDEN_ERROR.getMessage()).build();
            sendMsg(channel, errorResp);
        }
        sendByType(chatMessageVo, token, uid, channel);

    }

    @Override
    public void sendMessage(String token, WSBaseReq req) {
        // 发送数据
        String content = req.getData();
        ChatMessageVo chatMessageVo = JSONUtil.toBean(content, ChatMessageVo.class);
        // 接收消息 用户id
        Long uid = req.getUserId();
        sendByType(chatMessageVo, token, uid, null);

    }

    private void sendByType(ChatMessageVo chatMessageVo, String token, Long uid, Channel channel) {
        long loginUserId = Long.parseLong(StpUtil.getLoginIdByToken(token).toString());
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.of(chatMessageVo.getType());
        //发送消息
        switch (messageTypeEnum) {
            case CREATE_CHESS_ROOM:
                //创建棋局房间
                //自动生成房间号
                String roomId = String.valueOf(System.currentTimeMillis());
                CHESS_ROOM_MAP.putIfAbsent(roomId, new CopyOnWriteArrayList<>());
                CHESS_ROOM_MAP.get(roomId).add(channel);
                //返回房间号
                WSBaseResp<Object> createResp = WSBaseResp.builder().type(MessageTypeEnum.CREATE_CHESS_ROOM.getType()).data(roomId).build();
                sendMsg(channel, createResp);
                break;
            default:
                break;
        }
    }


    /**
     * 用户上线
     */
    private void online(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
    }

    /**
     * 如果在线列表不存在，就先把该channel放进在线列表
     *
     * @param channel 渠道
     * @return {@link WSChannelExtraDTO}
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    /**
     * 发送消息
     *
     * @param channel    渠道
     * @param wsBaseResp WS基础研究
     */
    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    /**
     * 用户下线
     * return 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollUtil.isNotEmpty(channels)) {
                channels.removeIf(channel1 -> channel1.equals(channel));
            }
            return CollUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
    }
}
