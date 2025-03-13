package com.cong.fishisland.manager;


import com.cong.fishisland.common.ErrorCode;
import com.cong.fishisland.common.exception.BusinessException;
import com.cong.fishisland.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MinioManager {
    @Resource
    MinioConfig minioConfig;
    @Resource
    MinioClient minioClient;

    //获取列表
    public List<String> listObjects() {
        List<String> list=new ArrayList<>();
        try {

            ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build();

            Iterable<Result<Item>> results =minioClient.listObjects(listObjectsArgs);
            for (Result<Item> result : results) {
                Item item = result.get();
                log.info(item.lastModified() + ", " + item.size() + ", " + item.objectName());
                list.add(item.objectName());
            }
        }catch (Exception e){
            log.error("错误："+e.getMessage());
        }
        return list;
    }

    //删除
    public void deleteObject(String objectName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        }catch (Exception e){
            log.error("错误："+e.getMessage());
        }
    }

    //上传
    public void uploadObject(InputStream is, String fileName, String contentType) {

        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .contentType(contentType)
                    .stream(is, is.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            is.close();
        }catch (Exception e){
            log.error("MinIO 文件上传失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "MinIO 文件上传失败");
        }
    }

    //获取minio中地址
    public String getObjectUrl(String objectName){
        try {
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .expiry(7, TimeUnit.DAYS)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        }catch (Exception e){
            e.printStackTrace();
            log.error("错误："+e.getMessage());
        }
        return "";
    }

    //下载minio服务的文件
    public InputStream getObject(String objectName){
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build();
            return minioClient.getObject(getObjectArgs);
        }catch (Exception e){
            log.error("错误："+e.getMessage());
        }
        return null;
    }

    /**
     生成上传预签名URL（PUT）
     * @param fileName 文件名
     * @return 预签名URL
     */
    public String generatePresignedUploadUrl(String fileName) {
        try {
            // 安全处理文件名（防止路径遍历）
            String safeFileName = sanitizeFileName(fileName);
            // 生成预签名URL（PUT方法）
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(minioConfig.getBucketName())
                            .object(safeFileName)
                            .expiry(15, TimeUnit.MINUTES) // 15分钟有效
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    /**
     * 生成下载预签名URL（GET）
     * @param fileName 文件名
     * @return 预签名URL
     */
    public String generatePresignedDownloadUrl(String fileName) {
        try {
            String safeFileName = sanitizeFileName(fileName);
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(safeFileName)
                            .expiry(1, TimeUnit.HOURS) // 1小时有效
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成预签名URL失败", e);
        }
    }

    // 文件名安全处理
    private String sanitizeFileName(String fileName) {
        // 过滤非法字符，防止路径遍历
        return fileName.replaceAll("[^a-zA-Z0-9-_.]", "");
    }
}
