package com.cong.fishisland.manager;


import cn.hutool.core.text.CharSequenceUtil;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.config.AIModelConfig;
import com.cong.fishisland.model.enums.ChatMessageRoleEnum;
import com.cong.fishisland.model.vo.ai.SiliconFlowRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用的 AI 调用类
 */
@Service
@Data
public class AiManager {

    private static final String DEFAULT_MODEL = "deepseek-v3-0324";

    private final RestTemplate restTemplate;

    private final AIModelConfig aiModelConfig;

    @Autowired
    public AiManager(RestTemplateBuilder restTemplateBuilder, AIModelConfig aiModelConfig) {
        this.aiModelConfig = aiModelConfig;
        this.restTemplate = restTemplateBuilder
                .defaultHeader("Authorization", "ccong")
                .defaultHeader("Content-Type", "application/json")
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
    }

    // 仅用户输入
    public String doChat(String userPrompt) {
        return doChat("", userPrompt, DEFAULT_MODEL);
    }

    // 系统提示 + 用户输入
    public String doChat(String systemPrompt, String userPrompt) {
        return doChat(systemPrompt, userPrompt, DEFAULT_MODEL);
    }

    // 完整参数版(系统提示 + 用户输入+ 模型)
    public String doChat(String systemPrompt, String userPrompt, String model) {
        List<SiliconFlowRequest.Message> messages = new ArrayList<>(2);
        if (CharSequenceUtil.isNotBlank(systemPrompt)) {
            SiliconFlowRequest.Message systemMessage = new SiliconFlowRequest.Message();
            systemMessage.setRole(ChatMessageRoleEnum.SYSTEM.getValue());
            systemMessage.setContent(systemPrompt);
            messages.add(systemMessage);
        }

        SiliconFlowRequest.Message userMessage = new SiliconFlowRequest.Message();
        userMessage.setRole(ChatMessageRoleEnum.USER.getValue());
        userMessage.setContent(userPrompt);
        messages.add(userMessage);

        return doChat(messages, model);
    }

    // 消息列表 + 默认模型
    public String doChat(List<SiliconFlowRequest.Message> messages) {
        return doChat(messages, DEFAULT_MODEL);
    }

    /**
     * 核心请求方法（支持自定义消息列表）
     */
    public String doChat(List<SiliconFlowRequest.Message> messages, String model) {
        // 构建请求体
        SiliconFlowRequest request = new SiliconFlowRequest();
        request.setModel(model);
        request.setMessages(messages);
        request.setStream(false);

        try {
            // 发送请求（使用泛型明确的 ParameterizedTypeReference）
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    aiModelConfig.getChutesAi2() + "/chat/completions",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,
                        "AI 请求失败，HTTP Status:  " + response.getStatusCode());
            }

            // 直接获取带泛型的响应体
            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 响应体为空");
            }
            //处理响应
            return extractContentFromResponse(body);

        } catch (HttpStatusCodeException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "AI 服务返回错误响应: " + e.getStatusCode() + "，Body: " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "AI 服务访问失败（连接/超时）： " + e.getMessage());
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "AI 请求异常： " + e.getMessage());
        }

    }

    // 通过方法拆分处理响应体
    @SuppressWarnings("unchecked")
    private String extractContentFromResponse(Map<String, Object> responseBody) {
        // 1. 获取 choices 列表
        Object choicesObj = responseBody.get("choices");
        if (!(choicesObj instanceof List)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,
                    "AI 响应中没有找到合法的 choices 字段");
        }
        List<Map<String, Object>> choices = (List<Map<String, Object>>) choicesObj;
        if (choices.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 响应中的 choices 列表为空");
        }
        // 2. 获取第一个 choice
        Map<String, Object> firstChoice = choices.get(0);

        // 3. 获取 message 对象
        Object messageObj = firstChoice.get("message");
        if (!(messageObj instanceof Map)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 响应中 message 字段格式错误");
        }
        Map<String, Object> message = (Map<String, Object>) messageObj;

        // 4. 获取 content
        Object contentObj = message.get("content");
        if (!(contentObj instanceof String)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 响应中 content 字段不存在或格式错误");
        }
        return (String) contentObj;
    }
}
