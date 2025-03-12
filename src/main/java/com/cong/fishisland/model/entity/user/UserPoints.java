package com.cong.fishisland.model.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户积分
 * @TableName user_points
 */
@TableName(value ="user_points")
@Data
public class UserPoints {
    /**
     * 用户 ID
     */
    @TableId(value = "userId")
    private Long userId;

    /**
     * 积分
     */
    @TableField(value = "points")
    private Integer points;

    /**
     * 已使用积分
     */
    @TableField(value = "usedPoints")
    private Integer usedPoints;

    /**
     * 用户等级
     */
    @TableField(value = "level")
    private Integer level;

    /**
     * 最后签到时间
     */
    @TableField(value = "lastSignInDate")
    private Date lastSignInDate;

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