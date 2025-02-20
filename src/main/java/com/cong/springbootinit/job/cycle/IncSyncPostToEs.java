package com.cong.springbootinit.job.cycle;

import com.cong.springbootinit.mapper.PostMapper;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 增量同步帖子到 es
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class IncSyncPostToEs {

    @Resource
    private PostMapper postMapper;



    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {

        log.info("IncSyncPostToEs end, total {}", 11);
    }
}
