package com.cong.fishisland.model.ws.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author cong
 */
@Data
public class MessageWrapper {
    @JsonProperty("message")
    private Message message;
}