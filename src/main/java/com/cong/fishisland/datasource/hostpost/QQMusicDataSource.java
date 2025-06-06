package com.cong.fishisland.datasource.hostpost;

import com.alibaba.fastjson.JSON;
import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.enums.CategoryTypeEnum;
import com.cong.fishisland.model.enums.UpdateIntervalEnum;
import com.cong.fishisland.model.vo.hot.HotPostDataVO;
import com.microsoft.playwright.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QQMusicDataSource implements DataSource {


    //提交
    @Override
    public HotPost getHotPost() {

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            // 创建浏览器上下文，并设置 userAgent
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
            );

            Page page = context.newPage();

            String url = "https://y.qq.com/n/ryqq/toplist/26";
            page.navigate(url);

            // 等待页面加载一段时间（或你可以使用 waitForSelector 替代）

            page.waitForSelector(".songlist__list", new Page.WaitForSelectorOptions().setTimeout(8000));

            String html = page.content();
            System.out.println(html);
            browser.close();

            Document document = Jsoup.parse(html);

            Elements songlist__cover = document.getElementsByClass("songlist__cover");

            int count = 1;

            List<HotPostDataVO> dataList = new ArrayList<>();
            for (Element item : songlist__cover) {
                HotPostDataVO vo = HotPostDataVO.builder().title(item.attr("title")).url(url + item.attr("href")).followerCount(count++).build();
                dataList.add(vo);
            }

            return HotPost.builder()
                    .sort(CategoryTypeEnum.MUSIC_HOT.getValue())
                    .category(CategoryTypeEnum.MUSIC_HOT.getValue())
                    .name("QQ音乐热歌榜")
                    .updateInterval(UpdateIntervalEnum.TWO_HOUR.getValue())
                    .iconUrl("https://api.oss.cqbo.com/moyu/user_avatar/1922893849325314049/ciGXlg1M-logo.png")
                    .hostJson(JSON.toJSONString(dataList.subList(0, Math.min(dataList.size(), 20))))
                    .typeName("QQ音乐")
                    .build();


        }
    }
}

