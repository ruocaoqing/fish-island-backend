package com.cong.fishisland.datasource.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.common.ResultUtils;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.config.AIModelConfig;
import com.cong.fishisland.model.vo.ai.AiResponse;
import com.cong.fishisland.model.vo.ai.ImageAIRequest;
import com.cong.fishisland.model.vo.ai.SiliconFlowRequest;
import com.cong.fishisland.model.vo.ai.SiliconFlowResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


/**
 * Silicon Flow 数据源
 *
 * @author cong
 * @date 2025/03/13
 */
@Component
@Slf4j
public class ChutesAI2DataSource implements AIChatDataSource {

    @Resource
    private AIModelConfig aiModelConfig;

    @Override
    public AiResponse getAiResponse(List<SiliconFlowRequest.Message> messages, String model) {
        return null;
    }

    @Override
    public AiResponse getAiResponse(String prompt, String model) {

        ImageAIRequest imageAiRequest = new ImageAIRequest();
        imageAiRequest.setModel(model);
        imageAiRequest.setPrompt(prompt);
        imageAiRequest.setSize("128x128");
        imageAiRequest.setResponse_format("b64_json");

        // 发送 HTTP 请求
        HttpResponse response = HttpRequest.post(aiModelConfig.getChutesAi2() + "/images/generations")
                .header("Authorization", "ccong")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(imageAiRequest))
                .execute();
        String result = response.body();
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray.isEmpty() || jsonArray.getJSONObject(0).getString("b64_json") == null) {
            return AiResponse
                    .builder()
                    .id("-1")
                    .aiName(model)
                    .answer("生成图片失败了，请稍后再试")
                    .build();
        }
        String b64Json = jsonArray.getJSONObject(0).getString("b64_json");

        // 将 base64 转换为图片文件
        try {
            byte[] imageBytes = Base64.getDecoder().decode(b64Json);
            File tempFile = File.createTempFile("generated_image", ".png");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(imageBytes);
            }

            // 创建 OkHttpClient
            OkHttpClient client = new OkHttpClient();

            // 创建 MultipartBody
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", tempFile.getName(),
                            RequestBody.create(MediaType.parse("image/png"), tempFile))
                    .build();

            // 创建请求
            Request request = new Request.Builder()
                    .url("https://i.111666.best/image")
                    .addHeader("Auth-Token", "YOUR-TOKEN")
                    .post(requestBody)
                    .build();

            // 发送请求
            String imgUrl = null;
            try (Response uploadResponse = client.newCall(request).execute()) {
                // 验证响应
                String responseBody = uploadResponse.body().string();
                //获取 src 字段
                JSONObject parseObject = JSONObject.parseObject(responseBody);
                imgUrl = parseObject.getString("src");
            }

            // 删除临时文件
            tempFile.delete();

            return AiResponse
                    .builder()
                    .id("-1")
                    .aiName(model)
                    .answer("https://i.111666.best" + imgUrl)
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate image", e);
        }
        return null;
    }

    @Override
    public AiResponse getAiResponse(String prompt) {
        return getAiResponse(prompt, "flux.1-dev");
    }
}
