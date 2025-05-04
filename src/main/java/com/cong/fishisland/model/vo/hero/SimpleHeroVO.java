package com.cong.fishisland.model.vo.hero;

import lombok.Data;

import java.io.Serializable;

/**
 * 英雄简略信息
 * @author 许林涛
 * @date 2025年05月03日 16:22
 */
@Data
public class SimpleHeroVO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 中文名(如苍)
     */
    private String cname;

    private static final long serialVersionUID = 1L;
}
