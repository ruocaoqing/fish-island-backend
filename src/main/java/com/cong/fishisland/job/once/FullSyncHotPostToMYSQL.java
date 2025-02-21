package com.cong.fishisland.job.once;


import com.cong.fishisland.manager.DataSourceRegistry;
import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.enums.HotDataKeyEnum;
import com.cong.fishisland.service.HotPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量更新热榜数据
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FullSyncHotPostToMYSQL implements CommandLineRunner {

    private final DataSourceRegistry dataSourceRegistry;
    private final HotPostService hotPostService;

    @Override
    public void run(String... args) {
        log.info("开始更新热榜数据...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<HotPost> hotPostList = HotDataKeyEnum.getValues().stream().map(key -> dataSourceRegistry.getDataSourceByType(key).getHotPost()).collect(Collectors.toList());
        // 保存到数据库
        hotPostService.saveBatch(hotPostList);
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("更新热榜数据完成，耗时：{}ms", totalTimeMillis);
    }
}
