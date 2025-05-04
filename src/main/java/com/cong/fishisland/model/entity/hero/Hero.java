package com.cong.fishisland.model.entity.hero;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 王者荣耀英雄详情表
 * @TableName hero
 */
@TableName(value ="hero")
@Data
public class Hero implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 英雄英文标识(如177)
     */
    private String ename;

    /**
     * 中文名(如苍)
     */
    private String cname;

    /**
     * 称号(如苍狼末裔)
     */
    private String title;

    /**
     * 上线时间
     */
    private Date releaseDate;

    /**
     * 新英雄标识(0常规/1新英雄)
     */
    private Integer newType;

    /**
     * 主定位(1战士/2法师/3坦克/4刺客/5射手/6辅助)
     */
    private Integer primaryType;

    /**
     * 副定位(1战士/2法师/3坦克/4刺客/5射手/6辅助)
     */
    private Integer secondaryType;

    /**
     * 皮肤列表(用|分隔，如苍狼末裔|维京掠夺者|苍林狼骑)
     */
    private String skins;

    /**
     * 官网详情页链接
     */
    private String officialLink;

    /**
     * 内部ID
     */
    private Long mossId;

    /**
     * 种族[yxzz_b8]
     */
    private String race;

    /**
     * 势力[yxsl_54]
     */
    private String faction;

    /**
     * 身份[yxsf_48]
     */
    private String identity;

    /**
     * 区域[qym_e7]
     */
    private String region;

    /**
     * 能量[nl_96]
     */
    private String ability;

    /**
     * 身高[sg_30]
     */
    private String height;

    /**
     * 经典台词[rsy_49]
     */
    private String quote;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}