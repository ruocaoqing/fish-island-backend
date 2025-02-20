package com.cong.springbootinit.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cong.springbootinit.common.TestBaseByLogin;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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


}
