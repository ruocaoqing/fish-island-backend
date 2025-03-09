package com.cong.fishisland.service.impl.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.model.dto.chat.MessageQueryRequest;
import com.cong.fishisland.model.entity.chat.RoomMessage;
import com.cong.fishisland.model.vo.chat.RoomMessageVo;
import com.cong.fishisland.service.RoomMessageService;
import com.cong.fishisland.mapper.chat.RoomMessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cong
 * @description 针对表【room_message(房间消息表)】的数据库操作Service实现
 * @createDate 2025-03-09 11:14:07
 */
@Service
public class RoomMessageServiceImpl extends ServiceImpl<RoomMessageMapper, RoomMessage>
        implements RoomMessageService {

    @Override
    public Page<RoomMessageVo> listMessageVoByPage(MessageQueryRequest messageQueryRequest) {
        Long roomId = messageQueryRequest.getRoomId();

        // 获取当前页码
        int current = messageQueryRequest.getCurrent();
        // 获取每页大小
        int size = messageQueryRequest.getPageSize();
        if (roomId == null) {
            // 创建新的分页对象，用于存储转换后的消息对象
            Page<RoomMessageVo> messageVoPage = new Page<>(0, size, 0);
            // 将转换后的消息对象列表设置为新的分页对象的记录
            messageVoPage.setRecords(null);
            return messageVoPage;
        }
        // 创建分页对象
        Page<RoomMessage> messagePage = this.page(new Page<>(current, size),
                // 创建查询条件对象
                new LambdaQueryWrapper<RoomMessage>().eq(RoomMessage::getRoomId, roomId).orderByDesc(RoomMessage::getCreateTime));
        //反转
        // 将消息列表转换为RoomMessageVo对象列表
        List<RoomMessageVo> chatMessageRespList = messagePage.getRecords().stream().map(item -> new RoomMessageVo().getVoByEntity(item))
                .collect(Collectors.toList());
        // 创建新的分页对象，用于存储转换后的消息对象
        Page<RoomMessageVo> messageVoPage = new Page<>(current, size, messagePage.getTotal());
        // 将转换后的消息对象列表设置为新的分页对象的记录
        messageVoPage.setRecords(chatMessageRespList);
        // 返回新的分页对象
        return messageVoPage;
    }
}




