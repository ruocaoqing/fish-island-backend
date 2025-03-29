package com.cong.fishisland.datasource.hostpost;

import com.alibaba.fastjson.JSON;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.enums.CategoryTypeEnum;
import com.cong.fishisland.model.enums.UpdateIntervalEnum;
import com.cong.fishisland.model.vo.hot.HotPostDataVO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 直播吧数据源
 *
 * @author shing
 */
@Slf4j
@Component
public class ZhiBo8DataSource implements DataSource {

    private static final String ZHIBO8_URL = "https://zhibo8.com/";

    @Override
    public HotPost getHotPost() {
        List<HotPostDataVO> dataList = new ArrayList<>();
        try {
            Document document = Jsoup.connect(ZHIBO8_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0")
                    .get();
            // 抓取篮球资讯
            extractNews(document, ".vct-box.lanqiu-news ._content a.list-item", dataList);
            // 抓取足球资讯
            extractNews(document, ".vct-box.zuqiu-news ._content a.list-item", dataList);

        } catch (Exception e) {
            log.error("获取数据失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "获取数据失败");
        }

        return HotPost.builder()
                .sort(CategoryTypeEnum.SPORTS.getValue())
                .name("直播吧体育热榜")
                .category(CategoryTypeEnum.SPORTS.getValue())
                .updateInterval(UpdateIntervalEnum.ONE_HOUR.getValue())
                .iconUrl("https://zhibo8.com/favicon.ico")
                .hostJson(JSON.toJSONString(dataList.subList(0, Math.min(dataList.size(), 20))))
                .typeName("直播吧")
                .build();
    }

    private void extractNews(Document document, String selector, List<HotPostDataVO> dataList) {
        Elements newsElements = document.select(selector);
        for (Element news : newsElements) {
            String title = news.text();
            String url = news.attr("href");
            dataList.add(HotPostDataVO.builder()
                    .title(title)
                    .url(url)
                    .followerCount(100)
                    .build());
        }
    }
}
