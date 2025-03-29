package com.cong.fishisland.model.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 头像框
 * @TableName avatar_frame
 */
@TableName(value ="avatar_frame")
@Data
public class AvatarFrame {
    /**
     * 头像框 ID
     */
    @TableId(value = "frameId", type = IdType.AUTO)
    private Long frameId;

    /**
     * 头像框名称
     */
    @TableField(value = "url")
    private String url;

    /**
     * 头像框名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 头像框所需兑换积分
     */
    @TableField(value = "points")
    private Integer points;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;
}