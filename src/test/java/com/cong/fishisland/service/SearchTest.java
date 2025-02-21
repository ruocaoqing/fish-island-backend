package com.cong.fishisland.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.common.TestBaseByLogin;
import com.cong.fishisland.model.vo.hot.HotPostDataVO;
import com.cong.fishisland.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class SearchTest extends TestBaseByLogin {
    @Test
    void searchZhiHuData() {
        String urlZhiHu = "https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=50&desktop=true";

        String result = HttpRequest.get(urlZhiHu).execute().body();
        JSONObject resultJson = (JSONObject) JSON.parse(result);
        JSONArray data = resultJson.getJSONArray("data");
        data.forEach(item -> {
            JSONObject jsonItem = (JSONObject) item;
            JSONObject target = jsonItem.getJSONObject("target");
            String title = target.getString("title");
            String url = target.getString("url");
            String followerCount = target.getString("follower_count");
            String excerpt = target.getString("excerpt");
            log.info("\n标题：{}，\n链接：{}，\n热度：{} 万，\n摘要：{}", title, url, followerCount, excerpt);

        });

    }

    @Test
    void weiboSearchTest() throws IOException {
        //获取tid
        String tidUrl = "https://passport.weibo.com/visitor/genvisitor";
        Map<String, Object> params = new HashMap<>();
        params.put("cb", "gen_callback");
        String str = HttpUtil.get(tidUrl, params, 3000);
        String quStr = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
        String tid = "";
        if (!quStr.isEmpty()) {
            JSONObject result = JSONObject.parseObject(quStr);
            if (result.getIntValue("retcode") == 20000000) {
                tid = result.getJSONObject("data").getString("tid");
                System.out.println("tid:" + tid);
            }
        }

        //获腹SUb,sUbp
        String subUrl = "https://passport.weibo.com/visitor/visitor";
        Map<String, Object> params2 = new HashMap<>();
        params2.put("a", "incarnate");
        params2.put("t", tid);
        params2.put("w", "3");
        params2.put("c", "100");
        params2.put("cb", "cross_domain");
        params2.put("from", "weibo");
        String str2 = HttpUtil.get(subUrl, params2, 3000);
        String resultStr = str2.substring(str2.indexOf("(") + 1, str2.indexOf(")"));
        String sub = "";
        String subp = "";
        if (!resultStr.isEmpty()) {
            JSONObject result = JSONObject.parseObject(resultStr);
            if (result.getIntValue("retcode") == 20000000) {
                sub = result.getJSONObject("data").getString("sub");
                subp = result.getJSONObject("data").getString("subp");
            }
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response;
        HttpGet request = new HttpGet("https://s.weibo.com/top/summary?cate=realtimehot");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        request.setHeader("Referer", "https://s.weibo.com/top/summary?cate=realtimehot");
        request.setHeader("Cookie", "SUB=" + sub + "; SUBP=" + subp + ";");

        response = httpClient.execute(request);
        String html = EntityUtils.toString(response.getEntity());

        Document document = Jsoup.parse(html);
        Element item = document.getElementsByTag("tbody").first();
        if (item != null) {
            Elements items = item.getElementsByTag("tr");
            for (Element tmp : items) {
                Element rankEle = tmp.getElementsByTag("td").first();
                Elements textEle = tmp.select(".td-02").select("a");
                Elements followerEle = tmp.select(".td-02").select("span");
                //过滤广告
                Elements rdEle = tmp.select(".td-02").select("span");
                if (!Objects.requireNonNull(rankEle).text().isEmpty() && !rdEle.text().isEmpty()) {
                    log.info("title: {}", textEle.text());
                    log.info("url: https://s.weibo.com{}", textEle.attr("href"));
                    log.info("followerCount: {}", followerEle.text());
                }
            }
        }
    }

    @Test
    void codeFatherSearchTest() throws IOException {
        String urlCodeFather = "https://api.codefather.cn/api/search/hot";
        HttpPost request = new HttpPost(urlCodeFather);
        // 创建请求体（body）
        String jsonBody = "{\"hiddenContent\": true, \"pageSize\": 20, \"type\": \"all_hot\"}";
        StringEntity entity = new StringEntity(jsonBody);  // 将 JSON 字符串转换为实体

        // 设置请求体
        request.setEntity(entity);
        // 添加常见的请求头
        request.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        request.setHeader(HttpHeaders.REFERER, "https://s.weibo.com/top/summary?cate=realtimehot");

        // 添加其他请求头
        request.setHeader("Access-Control-Allow-Credentials", "true");
        request.setHeader("Access-Control-Allow-Origin", "https://www.codefather.cn");
        request.setHeader("Access-Control-Expose-Headers", "*");
        request.setHeader("Content-Encoding", "gzip");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Date", "Fri, 21 Feb 2025 07:03:51 GMT");
        request.setHeader("Server", "www.tsycdn.com");
        request.setHeader("Strict-Transport-Security", "max-age=31536000");
        request.setHeader("Vary", "Accept-Encoding");
        request.setHeader("Vary", "Origin,Access-Control-Request-Method,Access-Control-Request-Headers");
        request.setHeader("X-Cloudbase-Request-Id", "af099400-1c70-4f1b-9e6c-9a67996adce4");
        request.setHeader("X-Cloudbase-Upstream-Status-Code", "200");
        request.setHeader("X-Cloudbase-Upstream-Timecost", "133");
        request.setHeader("X-Cloudbase-Upstream-Type", "Tencent-CBR");
        request.setHeader("X-Request-Id", "af099400-1c70-4f1b-9e6c-9a67996adce4");
        request.setHeader("X-Upstream-Status-Code", "200");
        request.setHeader("X-Upstream-Timecost", "133");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(request);
        String result = EntityUtils.toString(response.getEntity());
        JSONObject resultJson = (JSONObject) JSON.parse(result);
        JSONObject data = resultJson.getJSONObject("data");
        JSONArray records = data.getJSONObject("searchPage").getJSONArray("records");
        List<HotPostDataVO> dataList = records.stream().map(item -> {
            JSONObject jsonItem = (JSONObject) item;
            String title = jsonItem.getString("title");
            String recommendScore = jsonItem.getString("recommendScore");
            String id = jsonItem.getString("id");
            String url = "https://www.codefather.cn/post/" + id;
            String excerpt = jsonItem.getString("description");

            return HotPostDataVO.builder()
                    .title(title)
                    .url(url)
                    .followerCount(Integer.parseInt(StringUtils.extractNumber(recommendScore)) * 10)
                    .excerpt(excerpt)
                    .build();
        }).collect(Collectors.toList());

        log.info("dataList: {}", dataList);

    }


}
