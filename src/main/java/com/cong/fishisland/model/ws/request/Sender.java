package com.cong.fishisland.model.ws.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sender {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("points")
    private int points;

    @JsonProperty("level")
    private int level;

    @JsonProperty("isAdmin")
    private boolean isAdmin;

    @JsonProperty("region")
    private String region;
}