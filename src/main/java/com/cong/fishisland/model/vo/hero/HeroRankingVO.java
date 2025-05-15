package com.cong.fishisland.model.vo.hero;

import lombok.Data;

/**
 * @author 许林涛
 * @date 2025年05月15日 16:00
 */
@Data
public class HeroRankingVO {
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer score;
    private Long rank; // 可选字段
}
