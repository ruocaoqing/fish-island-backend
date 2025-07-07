package com.cong.fishisland.model.enums;

import cn.hutool.core.util.ObjectUtil;
import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 热榜类型枚举
 *
 * @author cong
 */
@Getter
public enum HotDataKeyEnum {
    ZHI_HU("知乎", "zhihu"),
    WEI_BO("微博", "weibo"),
    CODE_FATHER("编程导航", "CodeFather"),
    BILI_BILI("哔哩哔哩", "bilibili"),
    HU_PU_STREET("虎扑步行街", "HuPuStreet"),
    WY_CLOUD_MUSIC("网易云音乐", "WyCloudMusic"),
    DOU_YIN("抖音", "DouYin"),
    CS_DN("csdn", "CSDN"),
    JUE_JIN("掘金", "JueJin"),
    SM_ZDM("什么值得买", "SmZdm"),
    ZHI_BO_8("直播吧", "ZhiBo8"),
    TIE_BA("贴吧", "tieba"),
    PENG_PAI("澎湃", "thepaper"),
    TOU_TIAO("今日头条", "toutiao"),
    THREE_SIX("36K", "36kr"),
    QQ_MUSIC("QQ音乐", "QQMusic"),
    BAI_DU("百度", "baidu");


    private final String text;

    private final String value;

    HotDataKeyEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static HotDataKeyEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "热榜类型枚举不能为空");
        }
        for (HotDataKeyEnum anEnum : HotDataKeyEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "热榜类型枚举参数不存在，请在：[" + Arrays.stream(values()).map(item -> item.value + ":" + item.text).collect(Collectors.joining(",")) + "]中选择");
    }

    /**
     * 获取值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

}