package com.cong.fishisland.service.impl;

import com.alibaba.fastjson.JSON;
import com.cong.fishisland.config.AIModelConfig;
import com.cong.fishisland.model.entity.chat.ChatMessage;
import com.cong.fishisland.model.enums.ChatMessageRoleEnum;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlexChatServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(FlexChatServiceDemo.class);
    @Resource
    private WebClient webClient;
    @Resource
    private AIModelConfig aiModelConfig;

    public Flux<String> streamChat(String prompt) {
        return webClient.post()
                .uri(aiModelConfig.getChutesAi2() + "/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "ccong")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(buildRequestBody(prompt))
                .retrieve()
                .bodyToFlux(String.class)
                .takeUntil(line -> line.contains("[DONE]"))
                .map(chunk -> {
                    try {
                        log.info(chunk);

                        String json = chunk.substring("data:".length()).trim();
                        Map<String, Object> map = JSON.parseObject(chunk, Map.class);
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");
                        if (!choices.isEmpty()) {
                            Map<String, Object> delta = (Map<String, Object>) choices.get(0).get("delta");
                            Object content = delta.get("content");
                            return content != null ? content.toString() : "";
                        }
                    } catch (Exception e) {
                        log.error("Error parsing chunk: {}", chunk, e);
                    }
                    return "";
                })
                .filter(s -> !s.isEmpty());
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        List<ChatMessage> messages = new ArrayList<>(2);
        messages.add(ChatMessage.builder()
                .role(ChatMessageRoleEnum.USER)
                .content(prompt)
                .build());
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-v3-0324");
        requestBody.put("messages", messages.stream()
                .map(this::convertMessage)
                .collect(Collectors.toList()));
        requestBody.put("stream", true);
        return requestBody;
    }

    /**
     * 转换消息对象
     */
    private Map<String, String> convertMessage(ChatMessage message) {
        Map<String, String> map = new HashMap<>();
        map.put("role", message.getRole().toLowerCase());
        map.put("content", message.getContent());
        return map;
    }
}
