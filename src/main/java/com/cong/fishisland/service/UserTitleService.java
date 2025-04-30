package com.cong.fishisland.service;

import com.cong.fishisland.model.entity.user.UserTitle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author cong
* @description 针对表【user_title(用户称号)】的数据库操作Service
* @createDate 2025-04-30 10:07:06
*/
public interface UserTitleService extends IService<UserTitle> {
    /**
     * 获取用户可用的称号列表
     * @return 称号列表
     */
    List<UserTitle> listAvailableTitles();

    /**
     * 设置当前使用的称号
     * @param titleId 称号ID
     * @return 是否设置成功
     */
    Boolean setCurrentTitle(Long titleId);
}
