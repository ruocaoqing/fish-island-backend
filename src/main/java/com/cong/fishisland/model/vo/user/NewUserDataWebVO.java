package com.cong.fishisland.model.vo.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增用户数据
 * @author 许林涛
 * @date 2025年05月22日 10:10
 */
@Data
public class NewUserDataWebVO implements Serializable {
    /**
     * 日期
     */
    private String date;
    /**
     * 新增用户数量
     */
    private Integer newUserCount;
    private static final long serialVersionUID = 1L;
}