package com.cong.fishisland.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.fishisland.model.entity.hero.Hero;
import com.cong.fishisland.model.vo.hero.HeroVO;
import com.cong.fishisland.model.vo.hero.SimpleHeroVO;

import java.util.List;

/**
 * @author 许林涛
 * @description 针对表【hero(王者荣耀英雄详情表)】的数据库操作Service
 * @createDate 2025-05-02 16:47:41
 */
public interface HeroService extends IService<Hero> {

    Boolean addHeroList();

    Boolean updateHeroSkins(String ename,String skins);

    void updateHeroListSkins();

    HeroVO getRandomHero();

    HeroVO getNewHero();

    List<SimpleHeroVO> listSimpleHero();

    HeroVO getHeroById(Long id);
}
