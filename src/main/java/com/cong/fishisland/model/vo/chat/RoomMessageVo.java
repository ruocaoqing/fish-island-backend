package com.cong.fishisland.model.vo.chat;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cong.fishisland.model.entity.chat.RoomMessage;
import com.cong.fishisland.model.ws.request.MessageWrapper;
import lombok.Data;

/**
 * 房间消息表
 *
 * @author cong
 * @TableName room_message
 */
@Data
public class RoomMessageVo {
    /**
     * id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 房间 id
     */
    private Long roomId;

    /**
     * 消息 Json 数据
     */
    private MessageWrapper messageWrapper;

    public RoomMessageVo getVoByEntity(RoomMessage roomMessage) {

        RoomMessageVo roomMessageVo = new RoomMessageVo();

        roomMessageVo.setId(roomMessage.getId());
        roomMessageVo.setUserId(roomMessage.getUserId());
        roomMessageVo.setRoomId(roomMessage.getRoomId());
        roomMessageVo.setMessageWrapper(JSON.parseObject(roomMessage.getMessageJson(), MessageWrapper.class));

        return roomMessageVo;
    }
}