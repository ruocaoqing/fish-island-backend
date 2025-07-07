package com.cong.fishisland.datasource.hostpost;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.enums.CategoryTypeEnum;
import com.cong.fishisland.model.enums.UpdateIntervalEnum;
import com.cong.fishisland.model.vo.hot.HotPostDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 掘金数据源
 *
 * @author shing
 */
@Slf4j
@Component
public class PengPaiDataSource implements DataSource {

    private static final String PENG_PAI_URL = "https://cache.thepaper.cn/contentapi/wwwIndex/rightSidebar";

    private static final String PENG_PAI_POST_URL = "https://www.thepaper.cn/newsDetail_forward_";

    @Override
    public HotPost getHotPost() {
        List<HotPostDataVO> allDataList = new ArrayList<>();

        try {
            // 1. 构建请求URL
            URI url = new URIBuilder(PENG_PAI_URL)
                    .build();

            // 2. 发送请求并处理响应
            try (HttpResponse response = HttpRequest.get(url.toString())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36...")
                    .execute()) {

                String result = response.body();
                JSONObject resultJson = JSON.parseObject(result);
                JSONObject data = resultJson.getJSONObject("data");
                JSONArray hotNews = data.getJSONArray("hotNews");
                // 3. 解析数据
                hotNews.stream()
                        .map(JSONObject.class::cast)
                        .forEach(jsonItem -> {
                            try {


                                int contId = jsonItem.getIntValue("contId");
                                String title = jsonItem.getString("name");
                                int praiseTimes = jsonItem.getIntValue("praiseTimes");
                                allDataList.add(HotPostDataVO.builder()
                                        .title(title)
                                        .url(PENG_PAI_POST_URL + contId)
                                        .followerCount(praiseTimes)
                                        .build());

                            } catch (Exception e) {
                                log.warn("数据解析失败: {}", jsonItem.toJSONString(), e);
                            }
                        });
            }

        } catch (URISyntaxException e) {
            log.error("URL构造失败: {}", PENG_PAI_URL, e);
        } catch (Exception e) {
            log.error("未知错误", e);
        }

        // 4. 排序并返回
        return HotPost.builder()
                .category(CategoryTypeEnum.TECH_PROGRAMMING.getValue())
                .sort(CategoryTypeEnum.TECH_PROGRAMMING.getValue())
                .name("澎湃热榜")
                .updateInterval(UpdateIntervalEnum.HALF_HOUR.getValue())
                .iconUrl("https://lf3-cdn-tos.bytescm.com/obj/static/xitu_juejin_web//static/favicon.ico")
                .hostJson(JSON.toJSONString(allDataList.stream()
                        .sorted(Comparator.comparingInt(HotPostDataVO::getFollowerCount).reversed())
                        .collect(Collectors.toList())
                        .subList(0, Math.min(allDataList.size(), 20))))
                .typeName("澎湃")
                .build();
    }
}