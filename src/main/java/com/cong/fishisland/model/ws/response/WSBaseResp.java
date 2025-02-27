package com.cong.fishisland.model.ws.response;

import com.cong.fishisland.model.enums.WSReqTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: ws的基本返回信息体
 * @author cong
 * @date 2024/02/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WSBaseResp<T> {
    /**
     * ws推送给前端的消息
     *
     * @see WSReqTypeEnum
     */
    private String type;
    private T data;
}
