package com.cong.fishisland.datasource.hostpost;

import com.cong.fishisland.model.entity.hot.HotPost;

/**
 * 数据源接口（新接入数据源必须实现 ）
 *
 */
public interface DataSource {

    /**
     * 获取热榜数据
     *
     * @return {@link HotPost }
     */
    HotPost getHotPost();
}