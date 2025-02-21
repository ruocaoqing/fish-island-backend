package com.cong.fishisland.service.impl.hot;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.vo.hot.HotPostVO;
import com.cong.fishisland.service.HotPostService;
import com.cong.fishisland.mapper.host.HotPostMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cong
 * @description 针对表【hot_post(热点表)】的数据库操作Service实现
 * @createDate 2025-02-21 08:37:06
 */
@Service
public class HotPostServiceImpl extends ServiceImpl<HotPostMapper, HotPost>
        implements HotPostService {

    @Override
    public List<HotPostVO> getHotPostList() {
        return this.list().stream().map(HotPostVO::objToVo).collect(Collectors.toList());
    }
}




