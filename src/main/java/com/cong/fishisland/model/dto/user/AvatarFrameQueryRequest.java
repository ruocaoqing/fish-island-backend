package com.cong.fishisland.model.dto.user;

import com.cong.fishisland.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 头像框查询请求
 * @author cong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AvatarFrameQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
} 