package com.cong.fishisland.model.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 新增用户走势图请求
 * @author 许林涛
 * @date 2025年05月22日 10:19
 */
@Data
public class NewUserDataWebRequest {
    /**
     * 用户数据新增类型 0 - 每周新增，1 - 每月新增，2 - 每年新增 3 - 时间范围
     */
    private Integer type;

    /**
     * 出发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date beginTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date endTime;
}
