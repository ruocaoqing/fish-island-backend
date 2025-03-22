package com.cong.fishisland.model.vo;

import lombok.Data;

/**
 * @author cong
 */
@Data
public class WebParseVO {
    /**
     * 网页标题
     */
    private String title;
    
    /**
     * 网页描述
     */
    private String description;
    
    /**
     * 网站图标
     */
    private String favicon;
} 