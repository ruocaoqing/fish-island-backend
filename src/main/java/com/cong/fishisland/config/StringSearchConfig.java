package com.cong.fishisland.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toolgood.words.StringSearch;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 敏感词配置类
 * @author cong
 */
@Configuration
@Slf4j
public class StringSearchConfig {
    // 创建全局实例
    private final StringSearch wordsUtil = new StringSearch();

    @PostConstruct
    public void init() {
        List<String> keywords = loadKeywordsFromFile();
        // 初始化 StringSearch
        wordsUtil.SetKeywords(keywords);
        log.info("敏感词库初始化完成");
    }

    // 读取资源文件并解析成 List
    private List<String> loadKeywordsFromFile() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("key.txt")), StandardCharsets.UTF_8))) {
            return reader.lines()
                    .flatMap(line -> Arrays.stream(line.split("\\|")))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("读取关键词文件失败: {}", "key.txt", e);
        }
        return new ArrayList<>();
    }

    @Bean
    public StringSearch wordsUtil() {
        // 将 `wordsUtil` 注册为 Spring Bean
        return wordsUtil;
    }
}
