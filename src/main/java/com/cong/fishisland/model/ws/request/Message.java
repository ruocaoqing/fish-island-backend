package com.cong.fishisland.model.ws.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author cong
 */
@Data
public class Message {
    @JsonProperty("id")
    private String id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("sender")
    private Sender sender;

    @JsonProperty("timestamp")
    private String timestamp;
}