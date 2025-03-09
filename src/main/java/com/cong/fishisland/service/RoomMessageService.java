package com.cong.fishisland.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.fishisland.model.dto.chat.MessageQueryRequest;
import com.cong.fishisland.model.entity.chat.RoomMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.fishisland.model.vo.chat.RoomMessageVo;

/**
* @author cong
* @description 针对表【room_message(房间消息表)】的数据库操作Service
* @createDate 2025-03-09 11:14:07
*/
public interface RoomMessageService extends IService<RoomMessage> {

    Page<RoomMessageVo> listMessageVoByPage(MessageQueryRequest messageQueryRequest);
}
