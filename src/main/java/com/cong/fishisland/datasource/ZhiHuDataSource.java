package com.cong.fishisland.datasource;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.vo.hot.HotPostDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 知乎热榜数据源
 *
 * @author cong
 * @date 2025/02/21
 */
@Slf4j
@Component
public class ZhiHuDataSource implements DataSource {
    @Override
    public HotPost getHotPost() {
        String urlZhiHu = "https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=50&desktop=true";

        String result = HttpRequest.get(urlZhiHu).execute().body();
        JSONObject resultJson = (JSONObject) JSON.parse(result);
        JSONArray data = resultJson.getJSONArray("data");
        List<HotPostDataVO> dataList = data.stream().map(item -> {
            JSONObject jsonItem = (JSONObject) item;
            JSONObject target = jsonItem.getJSONObject("target");
            String title = target.getString("title");
            String url = target.getString("url");
            String followerCount = target.getString("follower_count");
            String excerpt = target.getString("excerpt");
            HotPostDataVO hotPostDataVO = HotPostDataVO.builder()
                    .title(title)
                    .url(url)
                    .followerCount(Integer.valueOf(followerCount))
                    .excerpt(excerpt)
                    .build();

            log.info("\n标题：{}，\n链接：{}，\n热度：{} 万，\n摘要：{}", title, url, followerCount, excerpt);

            return hotPostDataVO;
        }).collect(Collectors.toList());
        return HotPost.builder()
                .name("知乎热榜")
                .iconUrl("https://www.zhihu.com/favicon.ico")
                .hostJson(JSON.toJSONString(dataList))
                .typeName("知乎")
                .build();
    }
}
