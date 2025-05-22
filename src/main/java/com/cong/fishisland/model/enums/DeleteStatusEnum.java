package com.cong.fishisland.model.enums;

import lombok.Getter;

/**
 * 删除状态枚举类，用于表示实体是否被删除的状态
 */
@Getter
public enum DeleteStatusEnum {
    // 未删除状态，用0表示
    NOT_DELETED(0),
    // 已删除状态，用1表示
    DELETED(1);

    // 私有变量，存储枚举项的值
    private final int value;

    /**
     * 构造方法，初始化枚举项的值
     * @param value 枚举项的值
     */
    DeleteStatusEnum(int value) {
        this.value = value;
    }

}
