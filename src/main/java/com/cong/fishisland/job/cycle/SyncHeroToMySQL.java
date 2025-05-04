package com.cong.fishisland.job.cycle;

import cn.hutool.core.date.StopWatch;
import com.cong.fishisland.service.HeroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 同步英雄数据到MySQL
 * @author 许林涛
 * @date 2025年05月03日 8:06
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SyncHeroToMySQL {

    private final HeroService heroService;

    /**
     * 每周五 0 点执行一次
     */
    @Scheduled(cron = "0 0 0 ? * 5")
    public void updateHeroSkinsWeekly() {
        log.info("开始更新英雄皮肤数据...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            heroService.updateHeroListSkins();
            log.info("英雄皮肤数据更新成功");
        } catch (Exception e) {
            log.error("更新英雄皮肤数据失败", e);
        }

        stopWatch.stop();
        log.info("英雄皮肤数据更新完成，耗时：{}ms", stopWatch.getTotalTimeMillis());
    }
}
