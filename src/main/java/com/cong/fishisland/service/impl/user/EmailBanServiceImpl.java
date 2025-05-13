package com.cong.fishisland.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.fishisland.model.entity.user.EmailBan;
import com.cong.fishisland.service.EmailBanService;
import com.cong.fishisland.mapper.user.EmailBanMapper;
import org.springframework.stereotype.Service;

/**
* @author Shing
* @description 针对表【email_ban(邮箱封禁表)】的数据库操作Service实现
* @createDate 2025-05-13 16:51:41
*/
@Service
public class EmailBanServiceImpl extends ServiceImpl<EmailBanMapper, EmailBan>
    implements EmailBanService{

}




