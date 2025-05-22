package com.cong.fishisland.constant;

/**
 * 用户数据新增类型 0 - 每周新增，1 - 每月新增，2 - 每年新增 3 - 时间范围
 */
public interface NewUserDataTypeWebConstant {
    /**
     * 每周新增
     */
    Integer EVERY_WEEK = 0;
    /**
     * 每月新增
     */
    Integer EVERY_MONTH = 1;
    /**
     * 每年新增
     */
    Integer EVERY_YEAR = 2;
    /**
     * 时间范围
     */
    Integer TIME_RANGE = 3;
}
