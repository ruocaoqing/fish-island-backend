package com.cong.springbootinit.job.once;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

/**
 * 全量同步帖子到 es
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource

    @Override
    public void run(String... args) {

        log.info("FullSyncPostToEs end, total {}", 11);
    }
}
