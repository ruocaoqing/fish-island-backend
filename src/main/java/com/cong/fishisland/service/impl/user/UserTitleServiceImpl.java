package com.cong.fishisland.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.model.entity.user.UserTitle;
import com.cong.fishisland.service.UserTitleService;
import com.cong.fishisland.mapper.user.UserTitleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author cong
* @description 针对表【user_title(用户称号)】的数据库操作Service实现
* @createDate 2025-04-30 10:07:06
*/
@Service
public class UserTitleServiceImpl extends ServiceImpl<UserTitleMapper, UserTitle>
    implements UserTitleService{

    @Override
    public List<UserTitle> listAvailableTitles() {
        return null;
    }

    @Override
    public Boolean setCurrentTitle(Long titleId) {
        return null;
    }
}




