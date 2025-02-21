package com.cong.fishisland.service;

import com.cong.fishisland.model.entity.hot.HotPost;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.fishisland.model.vo.hot.HotPostVO;

import java.util.List;

/**
* @author cong
* @description 针对表【hot_post(热点表)】的数据库操作Service
* @createDate 2025-02-21 08:37:06
*/
public interface HotPostService extends IService<HotPost> {

    /**
     * 获取热门帖子列表
     *
     * @return {@link List }<{@link HotPostVO }>
     */
    List<HotPostVO> getHotPostList();
}
