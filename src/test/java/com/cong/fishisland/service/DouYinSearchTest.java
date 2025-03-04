package com.cong.fishisland.service;

import com.cong.fishisland.common.TestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GZIPInputStreamFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;

/**
 * 抖音搜索测试
 *
 * @author shing
 */
class DouYinSearchTest extends TestBase {

    @Test
    void DouYinTest() {
        // 配置参数
        final String HOT_BASE_URL = "https://www.douyin.com/hot";
        final String API_URL = "https://www.douyin.com/aweme/v1/web/hot/search/list";
        final int TIMEOUT = 30_000;

        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(10_000)
                        .setSocketTimeout(TIMEOUT)
                        .build())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setContentDecoderRegistry(Collections.singletonMap("gzip", GZIPInputStreamFactory.getInstance()))
                .build()) {

            // 2. 执行请求
            HttpGet httpGet = new HttpGet(API_URL);
            configureHeaders(httpGet); // 头信息配置抽离为方法

            try (CloseableHttpResponse response = client.execute(httpGet)) {
                handleResponse(response, HOT_BASE_URL);
            }
        } catch (IOException e) {
            handleException("网络通信异常", e);
        } catch (Exception e) {
            handleException("系统异常", e);
        }
    }

    private void configureHeaders(HttpGet httpGet) {
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)...");
        httpGet.setHeader("Referer", "https://www.douyin.com/hot");
        httpGet.setHeader("Accept", "application/json");
    }

    private void handleResponse(CloseableHttpResponse response, String hotUrl) throws IOException {
        String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JsonNode rootNode = new ObjectMapper().readTree(responseString);

        ArrayNode result = processItems(rootNode.path("data").path("word_list"), hotUrl);
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }

    private ArrayNode processItems(JsonNode items, String hotUrl) {
        ArrayNode resultArray = new ObjectMapper().createArrayNode();

        items.forEach(item -> {
            if (!validateItem(item)) return;

            ObjectNode entry;
            try {
                entry = createEntry(item, hotUrl);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            resultArray.add(entry);
        });

        return resultArray;
    }

    private boolean validateItem(JsonNode item) {
        return item.has("word") && item.has("hot_value") && item.has("sentence_id");
    }

    private ObjectNode createEntry(JsonNode item, String hotUrl) throws UnsupportedEncodingException {
        String title = item.get("word").asText();
        String encodedTitle = URLEncoder.encode(title, "UTF-8").replace("+", "%20");

        return new ObjectMapper().createObjectNode()
                .put("title", title)
                .put("hot_value", item.get("hot_value").asInt())
                .put("jump_url", String.format("%s/%s/%s",
                        hotUrl,
                        item.get("sentence_id").asText(),
                        encodedTitle))
                .put("position", item.path("position").asInt(0))
                .put("timestamp", Instant.now().getEpochSecond());
    }

    private void handleException(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        throw new RuntimeException(message, e);
    }
}