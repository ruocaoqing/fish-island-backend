package com.cong.fishisland.model.vo.file;

import com.tencent.cloud.Response;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cong
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CosCredentialVo {

    @ApiModelProperty(value = "请求响应")
    private Response response;

    @ApiModelProperty(value = "桶名称")
    private String bucket;

    @ApiModelProperty(value = "区域")
    private String region;

    @ApiModelProperty(value = "文件地址")
    private String key;
}
