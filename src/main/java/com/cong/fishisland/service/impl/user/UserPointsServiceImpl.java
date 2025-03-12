package com.cong.fishisland.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.model.entity.user.UserPoints;
import com.cong.fishisland.service.UserPointsService;
import com.cong.fishisland.mapper.user.UserPointsMapper;
import org.springframework.stereotype.Service;

/**
* @author cong
* @description 针对表【user_points(用户积分)】的数据库操作Service实现
* @createDate 2025-03-12 16:13:45
*/
@Service
public class UserPointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints>
    implements UserPointsService{

}




