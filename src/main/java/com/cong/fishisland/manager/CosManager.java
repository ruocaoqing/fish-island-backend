package com.cong.fishisland.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.model.vo.file.CosCredentialVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.cong.fishisland.config.CosClientConfig;

import java.io.File;
import java.util.TreeMap;
import java.util.UUID;
import javax.annotation.Resource;

import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Cos 对象存储操作
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Slf4j
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return {@link PutObjectResult}
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return {@link PutObjectResult}
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 获取凭证
     *
     * @return {@link Response }
     */
    public CosCredentialVo getCredential(String fileName) {
        if (!StpUtil.isLogin()) {
            return null;
        }
        TreeMap<String, Object> config = new TreeMap<>();
        try {
            // 云 api 密钥 SecretId
            config.put("secretId", cosClientConfig.getAccessKey());
            // 云 api 密钥 SecretKey
            config.put("secretKey", cosClientConfig.getSecretKey());

            // 5MB
            config.put("numeric_less_than", new JSONObject().put("cos:contentLength", 5 * 1024 * 1024));
            // 临时密钥有效时长，单位是秒
            config.put("durationSeconds", 1800);

            // 换成你的 bucket
            config.put("bucket", cosClientConfig.getBucket());
            // 换成 bucket 所在地区
            config.put("region", cosClientConfig.getRegion());

            // 可以通过 allowPrefixes 指定前缀数组, 例子： a.jpg 或者 a/* 或者 * (使用通配符*存在重大安全风险, 请谨慎评估使用)
            config.put("allowPrefixes", new String[]{
                    "fishMessage",
            });

            // 密钥的权限列表。简单上传和分片需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[]{
                    // 简单上传
                    "name/cos:PutObject",
                    "name/cos:PostObject",
                    // 分片上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);

            Response response = CosStsClient.getCredential(config);
            log.info("用户临时密钥获取成功，用户 ID：{}", StpUtil.getLoginId());

            return CosCredentialVo.builder()
                    .response(response)
                    .key("fishMessage/" + UUID.randomUUID() + "_" + fileName)
                    .region(cosClientConfig.getRegion())
                    .bucket(cosClientConfig.getBucket())
                    .build();
        } catch (Exception e) {
            log.error("获取临时密钥失败，原因：{}", e.getMessage());
            throw new IllegalArgumentException("no valid secret !");
        }
    }

}
