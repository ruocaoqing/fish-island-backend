package com.cong.fishisland.service;

import com.cong.fishisland.common.TestBaseByLogin;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class HuPuSearchTest extends TestBaseByLogin {

    static class Post {
        String title;
        String desc;
        String url;
        Integer hot;

        public Post(String title, String desc, String url, Integer hot) {
            this.title = title;
            this.desc = desc;
            this.url = url;
            this.hot = hot;
        }
    }

    @Test
    void test() {
        String hupuURL = "https://hupu.com";
        try {
            List<Post> posts = new ArrayList<>();

            // 获取主页面内容
            Document doc = Jsoup.connect(hupuURL)
                    .userAgent("Mozilla/5.0(Windows NT 10.0;Win64;x64;rv:66.0)Gecko/20100101 Firefox/66.0")
                    .get();

            // 获取每个帖子的信息
            Elements listItems = doc.select("div.list-item"); // 找到所有帖子项

            for (Element listItem : listItems) {
                // 获取帖子链接
                String postUrl = listItem.select("a.list-item-title").attr("href");

                // 获取帖子标题
                String title = listItem.select("div.item-title-conent").text();

                // 获取帖子描述（desc）
                String desc = listItem.select("div.list-item-desc").text();

                // 获取帖子热度
                String hotStr = listItem.select("div.list-item-lights").text();
                Integer hot = 0;
                if (hotStr != null && !hotStr.trim().isEmpty()) {
                    try {
                        // 使用正则表达式去掉非数字字符（例如 "亮"）
                        String numericHotStr = hotStr.replaceAll("[^0-9]", "").trim();
                        // 如果转换后的字符串非空，则转换为整数并乘以 1000
                        if (!numericHotStr.isEmpty()) {
                            hot = Integer.parseInt(numericHotStr) * 1000;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 如果热度信息为空，则设置热度为 0
                    hot = 0;
                }

                // 创建一个 Post 对象，并添加到列表中
                posts.add(new Post(title, desc, postUrl, hot));
            }

            // 使用 Gson 将列表转换成 JSON 格式
            Gson gson = new Gson();
            String jsonOutput = gson.toJson(posts);

            // 输出 JSON 格式的结果
            System.out.println(jsonOutput);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

