package com.cong.fishisland.datasource.hostpost;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.model.entity.hot.HotPost;
import com.cong.fishisland.model.enums.CategoryTypeEnum;
import com.cong.fishisland.model.enums.UpdateIntervalEnum;
import com.cong.fishisland.model.vo.hot.HotPostDataVO;
import com.cong.fishisland.utils.StringUtils;
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
        //带上请求头
        String result = HttpRequest.get(urlZhiHu)
                .header("cookie", "_xsrf=IzoujYI12xcUaFWOJlT8gnW1Pj9FWWQS; _zap=a0c9d561-f792-41c5-ab75-ac775836ebb0; d_c0=eJATQyJ5NBqPTtpbydn1bjaITzj_K0IO_W0=|1742978014; __snaker__id=RkgRPjOODyGBCIbR; q_c1=c09d19c0c90f4a8bb2d1ee48247f7994|1751434230000|1751434230000; __zse_ck=004_5N4aw9jvwI6V3bG6sH=W2Yr3YpfA57vi9zK0=Y=ZZ=DiMVdP44zPHDZlNf==F6MQmgS6PQXyr4Xt2migv6uQDxA7YxIrxfuSDAdcMWg3wDbi3F=OB46YyIE=ZBipMsMK-bT8w40h28AAHL+PsLH0lT35kcqPk8v4uSFa+JcmywsE4lFOonKN/ad9yd6ucXJP94LX5SstJBUDede9J/PkPxS1sWdOuLif+kT9lxmEwSbZ/E3eKl7MKOPfy8uCr5h9T; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1751434199,1751520716,1751850302; HMACCOUNT=F6B32A4B7B44110A; SESSIONID=zesMtH7Xjc66eGdWC4XPEB0JIJ6ARzPsjF5ABrVl2H2; JOID=W1sWAUNd9bYzoOwYLWPsqHpHnaE9LZDEdNWuL3Q2nIAEz6p2elCK1lCr5hkm-10XoY901FQWszrEMeXLSpfj3Wk=; osd=UF0XBkhW87c0q-ceLGTno3xGmqo2K5HDf96oLnM9l4YFyKF9fFGN3Vut5x4t8FsWpoR_0lURuDHCMOLAQZHi2mI=; DATE=1751875083798; cmci9xde=U2FsdGVkX1++iOVq0lmp2SRT5nf5M3t4TPx/L8AUv7O8oDST+D73KdK2k4BlbMvHrkvPlUUmD/sz+aIYcCTIIA==; pmck9xge=U2FsdGVkX1+/xicxV9KI37z2LJU1lG7je5LxcHNqkIQ=; assva6=U2FsdGVkX1/ST5ShLiy5tCV6If9xbxExLen/3OuVEnU=; assva5=U2FsdGVkX19KJVU9rAacMglmP/xV6Zq0f5I5CeZJaX2vi3NkqFw28cGn2kLV/s1Auz38VAUIOpl1o+kELLW5pg==; gdxidpyhxdE=1pWZVvlEKGpUxUWlW6niXdN4wqCySo2o2NyjaWtyzIop2iyBy%2BwhWVdBaQ7fKQOeM0Vk0D2DRVj%5C%2BjWkg3hwl%2BsAKxwJvE3Hv6V%5CXocYyNX%5CPOStL0sZszpvKxgSRMn9itHvJfHKgr9TuyIlTeNwAmd5uhYPLw6oHNh5prAkottlJqJb%3A1751875984026; crystal=U2FsdGVkX19HTaPLRjFZdnKCjrPJgagSCu7dtrFe4fXCEiT9KaPSDEcTbCmXXRW33fIFHMSy/mFOKbAADLSVMyPmaa2OlAnjiCMzaIHKnZ2NQFkaF6L5eqr2Ke32pMJO5GTu9Pi+wip/o2fljA+Wv9MUPRFRG4xhpxPvp0NmheKQLtERx11XMJpGUAZaqJPpucXb7AovlwnQHantfC2+kOzTtonN+YS4T5KIksBkwZGQyZr4T5sgqtxEWOb67SZw; vmce9xdq=U2FsdGVkX1/FZk3MpYgbu/T08o6lVdZDOaRqN7yPNtslzFsfeUiHEQqFuVPSg2u9CVddlX2zWqWu2UQFw431d4oEVfKiuLphQMkPtA673UQ22lZnbxH24ScdUUlyJWMzvBWJlV6vlmLzJTy05eLbiGPMIvcXjJKWoMHfXSCRgqM=; captcha_session_v2=2|1:0|10:1751875088|18:captcha_session_v2|88:TnQ1UWEzUkI0WWZCNWY0d01kSXdaMmR4c2lZcWRnc240b00vRVhwK2xJMzE3a1NoY3V0UXlQNythOHltNDRJNA==|004f60ec429f5bcee74c9ecc248e0a8c4fb098388e86e6d52e05ae90aaed37e6; z_c0=2|1:0|10:1751875168|4:z_c0|92:Mi4xcHhRZEJBQUFBQUI0a0JOREluazBHaVlBQUFCZ0FsVk5ZTXhZYVFESnBTUmZVdHBxeE4tYS1EX3FscWN4ejdNVkJR|5b1171cccf63c1a97a132da3f4f74553f9340e9ef571484a8e45c50e30239ba2; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1751875169; BEC=f7bc18b707cd87fca0d61511d015686f; tst=h")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("referer", "https://www.zhihu.com/hot")
                .header("accept", "application/json, text/plain, */*")
                .header("accept-language", "zh-CN,zh;q=0.9")
                .execute()
                .body();
        JSONObject resultJson = (JSONObject) JSON.parse(result);
        JSONArray data = resultJson.getJSONArray("data");
        List<HotPostDataVO> dataList = data.stream().map(item -> {
            JSONObject jsonItem = (JSONObject) item;
            JSONObject target = jsonItem.getJSONObject("target");
            String title = target.getString("title");
            String[] parts = target.getString("url").split("/");
            String url = "https://zhihu.com/question/" + parts[parts.length - 1];
            String followerCount = jsonItem.getString("detail_text");

            return HotPostDataVO.builder()
                    .title(title)
                    .url(url)
                    .followerCount(Integer.parseInt(StringUtils.extractNumber(followerCount)) * 10000)
                    .build();
        }).collect(Collectors.toList());
        return HotPost.builder()
                .sort(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .category(CategoryTypeEnum.GENERAL_DISCUSSION.getValue())
                .name("知乎热榜")
                .updateInterval(UpdateIntervalEnum.HALF_HOUR.getValue())
                .iconUrl("https://www.zhihu.com/favicon.ico")
                .hostJson(JSON.toJSONString(dataList.subList(0, Math.min(dataList.size(), 20))))
                .typeName("知乎")
                .build();
    }
}
