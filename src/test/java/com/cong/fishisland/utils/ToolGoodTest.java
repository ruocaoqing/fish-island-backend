package com.cong.fishisland.utils;

import com.cong.fishisland.common.TestBaseByLogin;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import toolgood.words.StringSearch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class ToolGoodTest extends TestBaseByLogin {

    @Resource
    private StringSearch wordsUtil;

    @Test
    void testToolGood() {
        String test = "我是中国人";
        List<String> list = new ArrayList<>();
        list.add("国人");
        list.add("zg人");
        System.out.println("StringSearch run Test.");

        StringSearch iwords = new StringSearch();
        iwords.SetKeywords(list);

        String str = iwords.Replace(test, '*');
        log.info("Replace result: {}", str);
        boolean b = iwords.ContainsAny(test);
        if (!b) {
            System.out.println("ContainsAny is Error.");
        }

        String f = iwords.FindFirst(test);
        if (f != "中国") {
            System.out.println("FindFirst is Error.");
        }

        List<String> all = iwords.FindAll(test);
        if (all.get(0) != "中国") {
            System.out.println("FindAll is Error.");
        }
        if (all.get(1) != "国人") {
            System.out.println("FindAll is Error.");
        }
        if (all.size() != 2) {
            System.out.println("FindAll is Error.");
        }


    }

    @Test
    void wordsUtilTest() {
        log.info(wordsUtil.Replace("我操你妈"));
    }
}
