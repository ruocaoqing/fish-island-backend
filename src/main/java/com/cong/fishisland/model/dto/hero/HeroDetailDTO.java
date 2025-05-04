package com.cong.fishisland.model.dto.hero;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
/**
 * 英雄详情DTO
 * @author 许林涛
 * @date 2025年05月02日 17:51
 */
@Data
public class HeroDetailDTO {
    @JsonProperty("da_ac")
    private List<BasicInfo> basicInfoList;

    @JsonProperty("yxyyhtx_8c")
    private List<VoiceAndImageInfo> voiceAndImageList;

    @JsonProperty("lb_a4")
    private List<LinkInfo> relatedLinks;

    @JsonProperty("cjjj_6c")
    private String heroDescription;

    @JsonProperty("yy_4e")
    private List<VoiceLine> voiceLines;

    @JsonProperty("bjyl_da")
    private String backgroundMusic;

    @JsonProperty("lsyxms_5c")
    private String historicalBackground;

    @Data
    public static class BasicInfo {
        @JsonProperty("YXMC_8f")
        private String heroName;

        @JsonProperty("yxbm_72")
        private String title;

        @JsonProperty("yxzz_b8")
        private String race;

        @JsonProperty("yxsl_54")
        private String faction;

        @JsonProperty("yxsf_48")
        private String identity;

        @JsonProperty("qym_e7")
        private String region;

        @JsonProperty("nl_96")
        private String ability;

        @JsonProperty("sg_30")
        private String height;

        @JsonProperty("rsy_49")
        private String quote;
    }

    @Data
    public static class VoiceAndImageInfo {
        @JsonProperty("yxcv_ff")
        private String cvName;

        @JsonProperty("hbtp_ee")
        private String avatarUrl;

        @JsonProperty("sbtp_ac")
        private String skillIconUrl;

        @JsonProperty("yy_4e")
        private List<VoiceLine> voiceClips;
    }

    @Data
    public static class LinkInfo {
        @JsonProperty("lbbanner_f4")
        private List<String> bannerUrls;

        @JsonProperty("lj_e9")
        private String linkUrl;
    }

    @Data
    public static class VoiceLine {
        @JsonProperty("yywa1_f2")
        private String dialogue;

        @JsonProperty("yyyp_9a")
        private String audioUrl;
    }
}
