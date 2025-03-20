package com.cong.fishisland.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.common.TestBaseByLogin;
import com.cong.fishisland.model.vo.hot.HotPostDataVO;
import com.cong.fishisland.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
class SearchTest extends TestBaseByLogin {
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
    void CsdnTest() {
        String csdnHotUrl = "https://blog.csdn.net/phoenix/web/blog/hot-rank";
        int pageSize = 25;
        int pagesNeeded = 5;
        for (int page = 0; page < pagesNeeded; page++) {
            // 构造请求 URL，自动翻页
            String url = csdnHotUrl + "?page=" + page + "&pageSize=" + pageSize + "&type=";
            // 发送 GET 请求并获取响应内容
            String result = HttpRequest.get(url).execute().body();
            // 解析 JSON 数据
            JSONObject resultJson = JSON.parseObject(result);
            JSONArray data = resultJson.getJSONArray("data");

            data.forEach(item -> {
                JSONObject jsonItem = (JSONObject) item;
                String title = jsonItem.getString("articleTitle");
                String articleDetailUrl = jsonItem.getString("articleDetailUrl");
                String hotRankScore = jsonItem.getString("hotRankScore");
                // 摘要字段由 title 限制 20 字修改而来
                String excerpt = title.length() > 20 ? title.substring(0, 20) : title;

                log.info("\n标题：{}，\n链接：{}，\n热度：{}，\n摘要：{}", title, articleDetailUrl, hotRankScore, excerpt);
            });

        }
    }

    @Test
    void JurJinSearchTest() throws URISyntaxException {

        String jueJinUrl = "https://api.juejin.cn/content_api/v1/content/article_rank";

        String jueJinPostUrl = "https://juejin.cn/post/";

        URI url = new URIBuilder(jueJinUrl)
                .addParameter("category_id", "1")
                .addParameter("type", "hot")
                .addParameter("aid", "2608")
                .addParameter("uuid", "7452631964433958441")
                .addParameter("spider", "0")
                .build();
        // 发送 GET 请求并获取响应内容
        String result = HttpRequest.get(String.valueOf(url)).execute().body();
        JSONObject resultJson = (JSONObject) JSON.parse(result);
        JSONArray data = resultJson.getJSONArray("data");
        // 解析数据
        List<JSONObject> sortedArticles = data.stream()
                .map(JSONObject.class::cast)
                .map(jsonItem -> {
                    JSONObject content = jsonItem.getJSONObject("content");
                    JSONObject contentCounter = jsonItem.getJSONObject("content_counter");

                    String title = content.getString("title");
                    String contentId = content.getString("content_id");
                    int hotRank = contentCounter.getIntValue("hot_rank");
                    String articleUrl = jueJinPostUrl + contentId;

                    // 构造 JSON 对象
                    JSONObject article = new JSONObject();
                    article.put("title", title);
                    article.put("hotRank", hotRank);
                    article.put("articleUrl", articleUrl);
                    return article;
                })
                .sorted((a, b) -> Integer.compare(b.getIntValue("hot_rank"), a.getIntValue("hot_rank"))) // 按 hot_rank 降序
                .collect(Collectors.toList());

        // 打印结果
        sortedArticles.forEach(article ->
                log.info("\n标题：{}\n热度：{}\n文章地址：{}",
                        article.getString("title"),
                        article.getIntValue("hotRank"),
                        article.getString("articleUrl"))
        );

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

            return HotPostDataVO.builder()
                    .title(title)
                    .url(url)
                    .followerCount(Integer.parseInt(StringUtils.extractNumber(recommendScore)) * 10)
                    .build();
        }).collect(Collectors.toList());

        log.info("dataList: {}", dataList);

    }

    @Test
    void WYCloudSearchTest() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response;
        HttpGet request = new HttpGet("https://music.163.com/discover/toplist?id=3778678");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

        try {
            response = httpClient.execute(request);
            String html = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(html);
            // 找到热门歌单class f-hide
            Element first = document.getElementsByClass("f-hide").first();
            first.select("a").forEach(item -> {
                String title = item.text();
                String url = item.attr("href");
                System.out.println(title + "url: " + "https://music.163.com" + url);
            });

        } catch (Exception e) {
            log.error("获取网易云热搜失败", e);
        }
    }

    @Test
    void smzdmTest() throws IOException {
        String smzdmUrl = "https://www.smzdm.com/top/";
        Document document = Jsoup.connect(smzdmUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0")
                .get();

        Elements items = document.select(".feed-hot-card");
        List<Map<String, String>> productList = new ArrayList<>();

        for (Element item : items) {
            String position = item.attr("data-position");
            Element link = item.select("a[target=_blank]").first();
            if (link == null) continue;
            String title = link.select(".feed-hot-title").text();
            String url = link.attr("href");
            String onclickData = link.attr("onclick");
            String floor = "";
            if (onclickData.contains("'floor':'好价品类榜'")) {
                floor = "好价品类榜";
            }
            if ("好价品类榜".equals(floor)) {
                Map<String, String> product = new HashMap<>();
                product.put("title", title);
                product.put("url", url);
                product.put("position", position);
                productList.add(product);
            }
        }
        productList.sort((p1, p2) ->
                Integer.compare(
                        Integer.parseInt(p1.get("position")),
                        Integer.parseInt(p2.get("position"))
                )
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(productList);
        System.out.println(jsonOutput);
        
    }

}
