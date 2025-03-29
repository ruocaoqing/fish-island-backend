package com.cong.fishisland.model.vo.ai;

import lombok.Data;

/**
 * @author cong
 */
@Data
public class ImageAIRequest {
    private String model;
    private String prompt;
    private String size;
    private String response_format;
}