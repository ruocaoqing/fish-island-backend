package com.cong.fishisland.datasource.hostpost;

import com.alibaba.fastjson.JSON;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 虎扑数据源
 *
 * @author shing
 */
@Slf4j
@Component
public class ThreeSixDataSource implements DataSource {

    private static final String POST_URL = "https://36kr.com";
    private static final String USER_AGENT = "Mozilla/5.0(Windows NT 10.0;Win64;x64;rv:66.0)Gecko/20100101 Firefox/66.0";
    String hotUrl = "https://36kr.com/hot-list/catalog";

    @Override
    public HotPost getHotPost() {
        Document document = fetchDocument(hotUrl);

        if (document == null) {
            log.error("无法获取虎扑步行街网页内容");
            return HotPost.builder().build();
        }

        List<HotPostDataVO> dataList = new ArrayList<>();
        Elements listItems = document.select("a.article-item-title");
        int index = 0;
        for (Element listItem : listItems) {
            index++;
            HotPostDataVO dataVO = extractPostData(listItem,index);
            if (dataVO != null) {
                dataList.add(dataVO);
            }
        }

        return HotPost.builder()
                .sort(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .name("36k热榜")
                .category(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .updateInterval(UpdateIntervalEnum.HALF_HOUR.getValue())
                .iconUrl("https://hupu.com/favicon.ico")
                .hostJson(JSON.toJSONString(dataList.stream()
                        .sorted((a, b) -> b.getFollowerCount() - a.getFollowerCount()).collect(Collectors.toList())
                        .subList(0, Math.min(dataList.size(), 20))))
                .typeName("36k")
                .build();
    }

    /**
     * 抓取网页内容
     *
     * @param url 网页 URL
     * @return Document 返回网页的 Document 对象
     */
    private Document fetchDocument(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .get();
        } catch (IOException e) {
            log.error("获取网页内容失败，URL: " + url, e);
            return null;
        }
    }

    /**
     * 提取帖子的具体信息
     *
     * @param listItem 每一个帖子项
     * @return 返回 HotPostDataVO 对象
     */
    private HotPostDataVO extractPostData(Element listItem,int  index) {
        try {
            String postUrl = listItem.attr("href");
            // 只有当 postUrl 为相对路径且有效时，才进行拼接并处理
            if (postUrl.trim().isEmpty() || postUrl.equals(POST_URL)) {
                return null;
            }
            // 拼接完整的帖子 URL
            postUrl = POST_URL + postUrl;

            String title = listItem.text();
            Integer hot = parseHot(listItem.select("span.t-lights").text());

            return HotPostDataVO.builder()
                    .title(title)
                    .url(postUrl)
                    .followerCount(1000- index)
                    .build();
        } catch (Exception e) {
            log.error("解析帖子数据失败", e);
            return null;
        }
    }

    /**
     * 解析热度数据
     *
     * @param hotStr 热度字符串
     * @return 返回处理后的热度值
     */
    private Integer parseHot(String hotStr) {
        if (hotStr == null || hotStr.isEmpty()) {
            return 0;
        }
        try {
            // 使用正则去除非数字字符
            String numericHotStr = hotStr.replaceAll("\\D", "").trim();
            // 转换为整数并乘以 1000
            return Integer.parseInt(numericHotStr) * 1000;
        } catch (NumberFormatException e) {
            log.error("解析热度数据失败: {}", hotStr, e);
            return 0;
        }
    }
}

