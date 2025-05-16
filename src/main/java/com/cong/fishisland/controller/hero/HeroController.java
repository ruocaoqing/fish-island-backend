package com.cong.fishisland.controller.hero;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.cong.fishisland.common.BaseResponse;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.constant.RedisKey;
import com.cong.fishisland.constant.UserConstant;
import com.cong.fishisland.model.entity.user.User;
import com.cong.fishisland.model.vo.hero.HeroRankingVO;
import com.cong.fishisland.model.vo.hero.HeroVO;
import com.cong.fishisland.model.vo.hero.SimpleHeroVO;
import com.cong.fishisland.service.HeroService;
import com.cong.fishisland.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


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

    /**
     * 记录猜对英雄次数
     * @return 是否记录成功
     */
    @PostMapping("/guess/success")
    public BaseResponse<Boolean> recordGuessSuccess() {
        User loginUser = userService.getLoginUser();
        Long userId = loginUser.getId();
        // 直接使用原子递增操作
        redisTemplate.opsForValue().increment(RedisKey.GUESS_HERO_SUCCESS_COUNT);
        // 记录排行榜
        redisTemplate.opsForZSet().incrementScore(
                RedisKey.GUESS_HERO_RANKING,
                userId.toString(),
                1
        );
        return ResultUtils.success(true);
    }

    /**
     * 获取猜对英雄次数
     * @return 猜对英雄次数
     */
    @GetMapping("/guess/count")
    public BaseResponse<Integer> getGuessCount() {
        Integer count = (Integer) redisTemplate.opsForValue().get(RedisKey.GUESS_HERO_SUCCESS_COUNT);
        return ResultUtils.success(count != null ? count : 0);
    }

    /**
     * 获取英雄排行榜
     * @return 英雄排行榜
     */
    @GetMapping("/guess/ranking")
    public BaseResponse<List<HeroRankingVO>> getGuessRanking() {
        // 获取分数最高的前10条数据
        Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(RedisKey.GUESS_HERO_RANKING, 0, 9);
        if (tuples == null || tuples.isEmpty()) {
            return ResultUtils.success(Collections.emptyList());
        }
        // 使用原子计数器维护排名
        AtomicLong rankCounter = new AtomicLong(1);
        // 转换数据结构
        List<HeroRankingVO> ranking = tuples.stream().map(tuple -> {
            HeroRankingVO vo = new HeroRankingVO();
            vo.setUserId(Long.parseLong(Objects.requireNonNull(tuple.getValue()).toString()));
            vo.setScore(Objects.requireNonNull(tuple.getScore()).intValue());
            // 获取用户信息
            User user = userService.getById(vo.getUserId());
            vo.setUserName(user.getUserName());
            vo.setUserAvatar(user.getUserAvatar());
            // 设置排名（从1开始递增）
            vo.setRank(rankCounter.getAndIncrement());
            return vo;
        }).collect(Collectors.toList());

        return ResultUtils.success(ranking);
    }


}
