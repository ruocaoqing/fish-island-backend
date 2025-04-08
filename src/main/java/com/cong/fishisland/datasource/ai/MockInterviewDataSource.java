package com.cong.fishisland.datasource.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.config.AIModelConfig;
import com.cong.fishisland.model.vo.ai.AiResponse;
import com.cong.fishisland.model.vo.ai.SiliconFlowRequest;
import com.cong.fishisland.model.vo.ai.SiliconFlowResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 模拟面试 AI 数据源
 *
 * @author Shing
 */
@Component
@Data
@RequiredArgsConstructor
public class MockInterviewDataSource implements AIChatDataSource {

    private static final String DEFAULT_MODEL = "deepseek-v3-0324";

    private final AIModelConfig aiModelConfig;

    @Override
    public AiResponse getAiResponse(List<SiliconFlowRequest.Message> messages, String model) {
        // 构建请求体
        SiliconFlowRequest request = new SiliconFlowRequest();
        request.setModel(model);
        request.setMessages(messages);

        // 发送请求
        HttpResponse response = HttpRequest.post(aiModelConfig.getChutesAi2() + "/chat/completions")
                .header("Authorization",  "ccong")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(request))
                .timeout(60000) // 超时时间
                .execute();

        if (response.getStatus() != 200) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,
                    "AI 请求失败，HTTP Status:  " + response.getStatus());
        }

        SiliconFlowResponse siliconFlowResponse = JSON.parseObject(response.body(), SiliconFlowResponse.class);
        // 拼接所有 choice 消息
        StringBuilder allMessage = new StringBuilder();
        for (SiliconFlowResponse.Choice choice : siliconFlowResponse.getChoices()) {
            allMessage.append(choice.getMessage().getContent());
        }
        //处理响应
        String[] result = allMessage.toString().split("</think>");
        return AiResponse
                .builder()
                .id(String.valueOf(siliconFlowResponse.getCreated()))
                .aiName(siliconFlowResponse.getModel())
                .answer(result.length > 1 ? result[1].trim() : result[0].trim())
                .build();
    }


    @Override
    public AiResponse getAiResponse(String prompt, String model) {
        // 构建单条消息
        SiliconFlowRequest.Message message = new SiliconFlowRequest.Message();
        message.setRole("user");
        message.setContent(prompt);
        return getAiResponse(Collections.singletonList(message), model);
    }

    @Override
    public AiResponse getAiResponse(String prompt) {
        return getAiResponse(prompt, DEFAULT_MODEL);
    }
}
