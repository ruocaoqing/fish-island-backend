package com.cong.fishisland.controller.hero;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.cong.fishisland.common.BaseResponse;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.constant.UserConstant;
import com.cong.fishisland.model.vo.hero.HeroVO;
import com.cong.fishisland.model.vo.hero.SimpleHeroVO;
import com.cong.fishisland.service.HeroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 王者荣耀英雄控制器
 * @author 许林涛
 * @date 2025年05月02日 16:50
 */
@RestController
@RequestMapping("/hero")
@Slf4j
public class HeroController {
    @Resource
    private HeroService heroService;

    /**
     * 初始化英雄列表（仅限管理员使用）
     * @return
     */
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    public BaseResponse<Boolean> addHeroList() {
        return ResultUtils.success(heroService.addHeroList());
    }

    /**
     * 随机获取一个英雄数据
     */
    @GetMapping("/get/random")
    public BaseResponse<HeroVO> getRandomHero() {
        return ResultUtils.success(heroService.getRandomHero());
    }

    /**
     * 获取最新英雄数据
     */
    @GetMapping("/get/new")
    public BaseResponse<HeroVO> getNewHero() {
        return ResultUtils.success(heroService.getNewHero());
    }

    /**
     * 简单英雄数据列表
     */
    @GetMapping("/list/simple")
    public BaseResponse<List<SimpleHeroVO>> listSimpleHero() {
        return ResultUtils.success(heroService.listSimpleHero());
    }

    /**
     * 通过id获取一个英雄数据
     */
    @GetMapping("/get")
    public BaseResponse<HeroVO> getHeroById(Long id) {
        return ResultUtils.success(heroService.getHeroById(id));
    }

}
