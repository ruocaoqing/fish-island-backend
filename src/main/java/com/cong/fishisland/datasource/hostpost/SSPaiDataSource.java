package com.cong.fishisland.datasource.hostpost;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.enums.CategoryTypeEnum;
import com.cong.fishisland.model.enums.UpdateIntervalEnum;
import com.cong.fishisland.model.vo.hot.HotPostDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 百度贴吧热榜
 *
 * @author Shing
 */
@Slf4j
@Component
public class SSPaiDataSource implements DataSource{
    String postUrl = "https://sspai.com/post";
    @Override
    public HotPost getHotPost() {

        String sspHotUrl = "https://sspai.com/api/v1/article/tag/page/get?limit=30&tag=%E7%83%AD%E9%97%A8%E6%96%87%E7%AB%A0";

        String result = HttpUtil.get(sspHotUrl);

        List<HotPostDataVO> dataList = parseSspTopicList(result);

        List<HotPostDataVO> sortedTopList = dataList.stream()
                .sorted(Comparator.comparingInt(HotPostDataVO::getFollowerCount).reversed())
                .limit(30)
                .collect(Collectors.toList());

        return HotPost.builder()
                .sort(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .name("夸克热搜")
                .category(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .updateInterval(UpdateIntervalEnum.HALF_HOUR.getValue())
                .iconUrl("https://tieba.baidu.com/favicon.ico")
                .hostJson(JSON.toJSONString(sortedTopList))
                .typeName("夸克热搜")
                .build();
    }

    private List<HotPostDataVO> parseSspTopicList(String json) {
        JSONObject resultJson = JSON.parseObject(json);
        JSONArray topicList = Optional.ofNullable(resultJson)
                .map(result  -> result .getJSONArray("data"))
                .orElseThrow(() -> new RuntimeException("数据结构异常，未获取到 topic_list"));
        int index = 0;
        ArrayList<HotPostDataVO> hotPostDataVOS = new ArrayList<>();
        for (Object item : topicList) {
            index++;
            JSONObject jsonItem = (JSONObject) item;
            HotPostDataVO build = HotPostDataVO.builder()
                    .title(jsonItem.getString("title"))
                    .url(postUrl+jsonItem.getString("id"))
                    .followerCount(1000 - index)
                    .build();
            hotPostDataVOS.add(build);
        }
        return hotPostDataVOS;
    }
}
