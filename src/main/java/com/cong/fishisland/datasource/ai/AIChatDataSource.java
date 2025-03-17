package com.cong.fishisland.datasource.ai;

import com.cong.fishisland.model.vo.ai.AiResponse;
import com.cong.fishisland.model.vo.ai.SiliconFlowRequest;

import java.util.List;

/**
 * AI数据源接口（新接入数据源必须实现 ）
 * @author Cong
 */
public interface AIChatDataSource {

    AiResponse getAiResponse(List<SiliconFlowRequest.Message> messages, String model);

    /**
     * 获取 AI 返回结果
     *
     * @param prompt 输入提示
     * @param model  模型类型
     */
    AiResponse getAiResponse(String prompt, String model);

    /**
     * 获取 AI 返回结果
     *
     * @param prompt 输入提示
     */
    AiResponse getAiResponse(String prompt);
}