package com.cong.fishisland.service;

import com.cong.fishisland.common.TestBase;
import com.cong.fishisland.datasource.ai.AIChatDataSource;
import com.cong.fishisland.model.vo.ai.AiResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

@Slf4j
class AITest extends TestBase {

    @Resource
    private AIChatDataSource aiChatDataSource;
    @Test
    void testSiliconFlowDataSource() {
        AiResponse response = aiChatDataSource.getAiResponse("今天会下雨吗？", "internlm/internlm2_5-20b-chat");
        log.info(response.toString());
        log.info(response.getAnswer());
        Assertions.assertNotNull(response.getAnswer());
    }
}
