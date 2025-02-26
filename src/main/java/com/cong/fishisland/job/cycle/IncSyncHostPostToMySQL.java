package com.cong.fishisland.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cong.fishisland.manager.DataSourceRegistry;

import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.enums.HotDataKeyEnum;
import com.cong.fishisland.service.HotPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Date;

/**
 * 自动同步热榜数据
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class IncSyncHostPostToMySQL {

    private final DataSourceRegistry dataSourceRegistry;
    private final HotPostService hotPostService;

    /**
     * 每半小时执行一次
     */
    @Scheduled(fixedRate = 1_800_000)
    public void run() {
        log.info("开始更新热榜数据...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HotDataKeyEnum.getValues().forEach(key -> {
            HotPost hotPost = dataSourceRegistry.getDataSourceByType(key).getHotPost();
            hotPost.setType(key);
            LambdaQueryWrapper<HotPost> hotPostLambdaQueryWrapper = new LambdaQueryWrapper<>();
            hotPostLambdaQueryWrapper.eq(HotPost::getType, key);
            HotPost oldHotPost = hotPostService.getOne(hotPostLambdaQueryWrapper);
            if (oldHotPost != null) {
                hotPost.setId(oldHotPost.getId());
            }
            hotPost.setUpdateTime(new Date());
            hotPostService.saveOrUpdate(hotPost);
        });
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("更新热榜数据完成，耗时：{}ms", totalTimeMillis);
    }
}
