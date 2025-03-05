package com.cong.fishisland.model.enums;

import cn.hutool.core.util.ObjectUtil;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 热榜分类枚举
 *
 * @author cong
 */
@Getter
public enum CategoryTypeEnum {
    GENERAL_DISCUSSION("综合资讯 & 讨论社区", 1),
    TECH_PROGRAMMING("技术 & 编程", 2),
    VIDEO_ENTERTAINMENT("视频 & 娱乐", 3),
    MUSIC_HOT("音乐热榜", 4);


    private final String text;

    private final Integer value;

    CategoryTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static CategoryTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtil.isEmpty(value)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "热榜更新间隔枚举不能为空");
        }
        for (CategoryTypeEnum anEnum : CategoryTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "热榜更新间隔枚举参数不存在，请在：[" + Arrays.stream(values()).map(item -> item.value + ":" + item.text).collect(Collectors.joining(",")) + "]中选择");
    }

    /**
     * 获取值列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

}