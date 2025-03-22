package com.cong.fishisland.service;

import com.cong.fishisland.model.vo.WebParseVO;

/**
 * @author cong
 */
public interface WebParserService {
    /**
     * 解析网页信息
     *
     * @param url 网页URL
     * @return 解析结果
     */
    WebParseVO parseWebPage(String url);
} 