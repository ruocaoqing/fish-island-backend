package com.cong.fishisland.model.dto.hero;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 英雄信息
 * @author 许林涛
 * @date 2025年05月02日 17:02
 */
@Data
public class HeroDTO {
    @JsonProperty("ename")
    private Integer ename;

    @JsonProperty("cname")
    private String cname;

    @JsonProperty("id_name")
    private String idName;

    @JsonProperty("title")
    private String title;

    @JsonProperty("time")
    private String time;

    @JsonProperty("new_type")
    private Integer newType;

    @JsonProperty("hero_type")
    private Integer heroType;

    @JsonProperty("hero_type2")
    private Integer heroType2;

    @JsonProperty("skin_name")
    private String skinName;

    @JsonProperty("m_bl_link")
    private String mBlLink;

    @JsonProperty("moss_id")
    private Integer mossId;
}
