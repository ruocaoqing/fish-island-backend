package com.cong.fishisland.model.ws.response;

import lombok.Data;

/**
 * @author cong
 */
@Data
public class DrawPlayer {
    /**
     * 玩家 ID
     */
    private String id;

    /**
     * 玩家昵称
     */
    private String userName;
    /**
     * 玩家头像
     */
    private String userAvatar;
    /**
     * 玩家分数
     * 默认为 0
     */
    private Integer score = 0;
    /**
     * 是否绘画
     */
    private Boolean isDrawing = false;
}
