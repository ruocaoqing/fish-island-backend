package com.cong.fishisland.model.vo.user;

import com.cong.fishisland.model.entity.user.AvatarFrame;
import lombok.Data;

import java.io.Serializable;

/**
 * 头像框视图对象
 * @author cong
 */
@Data
public class AvatarFrameVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 头像框名称
     */
    private String name;

    /**
     * 头像框图片地址
     */
    private String url;

    /**
     * 所需积分
     */
    private Integer points;

    /**
     * 是否拥有
     */
    private Boolean hasOwned = false;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param avatarFrame 头像框
     * @return {@link AvatarFrameVO}
     */
    public static AvatarFrameVO objToVo(AvatarFrame avatarFrame) {
        if (avatarFrame == null) {
            return null;
        }
        AvatarFrameVO avatarFrameVO = new AvatarFrameVO();
        avatarFrameVO.setId(avatarFrame.getFrameId());
        avatarFrameVO.setName(avatarFrame.getName());
        avatarFrameVO.setUrl(avatarFrame.getUrl());
        avatarFrameVO.setPoints(avatarFrame.getPoints());
        return avatarFrameVO;
    }
} 