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
public class BaiduDataSource implements DataSource {

    private static final String HUPU_URL = "https://bbs.hupu.com";
    private static final String USER_AGENT = "Mozilla/5.0(Windows NT 10.0;Win64;x64;rv:66.0)Gecko/20100101 Firefox/66.0";
    String baiduURL = "https://top.baidu.com/board?tab=realtime";

    @Override
    public HotPost getHotPost() {
        Document document = fetchDocument(baiduURL);

        if (document == null) {
            log.error("无法获取虎扑步行街网页内容");
            return HotPost.builder().build();
        }

        List<HotPostDataVO> dataList = new ArrayList<>();
        Elements listItems = document.select("div.content_1YWBm");
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
                .name("百度热搜")
                .category(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .updateInterval(UpdateIntervalEnum.HALF_HOUR.getValue())
                .iconUrl("https://hupu.com/favicon.ico")
                .hostJson(JSON.toJSONString(dataList.stream()
                        .sorted((a, b) -> b.getFollowerCount() - a.getFollowerCount()).collect(Collectors.toList())
                        .subList(0, Math.min(dataList.size(), 20))))
                .typeName("百度")
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
            String postUrl = listItem.select("a").attr("href");

            String title = listItem.select("div.c-single-text-ellipsis").text();

            return HotPostDataVO.builder()
                    .title(title)
                    .url(postUrl)
                    .followerCount(1000-index)
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

