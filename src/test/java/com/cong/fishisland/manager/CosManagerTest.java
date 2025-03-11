package com.cong.fishisland.manager;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.cong.fishisland.config.CosClientConfig;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import com.tencent.cloud.Scope;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Cos 操作测试
 *
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@SpringBootTest
class CosManagerTest {

    @Resource
    private CosManager cosManager;
    @Resource
    private CosClientConfig cosClientConfig;

    @Test
    void putObject() {
        cosManager.putObject("test", "test.json");
    }

    @Test
    void testScopesPolicy() {
        Scope scope = new Scope("name/cos:PutObject", "test-1250000000", "ap-guangzhou", "/test.txt");
        List<Scope> scopes = new LinkedList<Scope>();
        scopes.add(scope);

        System.out.println(CosStsClient.getPolicy(scopes));
    }

    @Test
    void testScopesPolicy2() {
        List<Scope> scopes = new LinkedList<Scope>();

        Scope scope = new Scope("name/cos:PutObject", "test-1250000000", "ap-guangzhou", "/test.txt");
        scopes.add(scope);
        scope = new Scope("name/cos:GetObject", "test-1250000000", "ap-guangzhou", "/test.txt");
        scopes.add(scope);
        scope = new Scope("name/cos:HeadObject", "test-1250000000", "ap-guangzhou", "/test.txt");
        scopes.add(scope);

        System.out.println(CosStsClient.getPolicy(scopes));
    }

    /**
     * 基本的临时密钥申请示例，适合对一个桶内的一批对象路径，统一授予一批操作权限
     */
    @Test
    void testGetCredential() {
        TreeMap<String, Object> config = new TreeMap<>();

        try {

            // 云 api 密钥 SecretId
            config.put("secretId",cosClientConfig.getAccessKey());
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
            config.put("allowPrefixes", new String[] {
                    "fishMessage",
            });

            // 密钥的权限列表。简单上传和分片需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
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
            System.out.println(response.credentials.tmpSecretId);
            System.out.println(response.credentials.tmpSecretKey);
            System.out.println(response.credentials.sessionToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("no valid secret !");
        }
    }
}