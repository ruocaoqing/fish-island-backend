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
public class TouTiaoDataSource implements DataSource {

        private static final String TOUTIAO_URL = "https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc";


    @Override
    public HotPost getHotPost() {
        List<HotPostDataVO> allDataList = new ArrayList<>();

        try {
            // 1. 构建请求URL
            URI url = new URIBuilder(TOUTIAO_URL)
                    .build();

            // 2. 发送请求并处理响应
            try (HttpResponse response = HttpRequest.get(url.toString())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36...")
                    .execute()) {

                String result = response.body();
                JSONObject resultJson = JSON.parseObject(result);
                JSONArray data = resultJson.getJSONArray("data");

                // 3. 解析数据
                data.stream()
                        .map(JSONObject.class::cast)
                        .forEach(jsonItem -> {
                            try {
                                String postUrl = jsonItem.getString("Url");
                                String title = jsonItem.getString("Title");
                                int followerCount = jsonItem.getIntValue("HotValue");
                                allDataList.add(HotPostDataVO.builder()
                                        .title(title)
                                        .url(postUrl)
                                        .followerCount(followerCount)
                                        .build());

                            } catch (Exception e) {
                                log.warn("数据解析失败: {}", jsonItem.toJSONString(), e);
                            }
                        });
            }

        } catch (URISyntaxException e) {
            log.error("URL构造失败: {}", TOUTIAO_URL, e);
        } catch (Exception e) {
            log.error("未知错误", e);
        }

        // 4. 排序并返回
        return HotPost.builder()
                .category(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .sort(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .name("今日头条")
                .updateInterval(UpdateIntervalEnum.HALF_HOUR.getValue())
                .iconUrl("https://lf3-cdn-tos.bytescm.com/obj/static/xitu_juejin_web//static/favicon.ico")
                .hostJson(JSON.toJSONString(allDataList.stream()
                        .sorted(Comparator.comparingInt(HotPostDataVO::getFollowerCount).reversed())
                        .collect(Collectors.toList())
                        .subList(0, Math.min(allDataList.size(), 20))))
                .typeName("头条")
                .build();
    }
}