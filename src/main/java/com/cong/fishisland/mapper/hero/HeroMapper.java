package com.cong.fishisland.mapper.hero;

import com.cong.fishisland.model.entity.hero.Hero;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 许林涛
* @description 针对表【hero(王者荣耀英雄详情表)】的数据库操作Mapper
* @createDate 2025-05-02 16:47:41
* @Entity com.cong.fishisland.model.entity.hero.Hero
*/
public interface HeroMapper extends BaseMapper<Hero> {

    /**
     * 随机查询英雄
     * @return 英雄数据
     */
    Hero selectRandomHero();

    /**
     * 查询最新英雄
     * @return 英雄数据
     */
    Hero selectNewestHero();
}




