package com.bootzero.big_event.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 检查存储桶是否存在，不存在则创建
     */
    private void ensureBucketExists() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if(!found){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("{}Bucket Created successfully", bucketName);
        }else {
            log.info("{}Bucket already exists", bucketName);
        }
    }

    /**
     * 上传文件
     *
     * @param file        MultipartFile 文件对象
     * @param objectName  存储在 MinIO 中的对象名称 (可以包含路径，例如 "images/myphoto.jpg")
     *                    如果不指定，可以使用 file.getOriginalFilename()
     * @return 返回存储的对象名称
     */
    public String uploadFile(MultipartFile file,String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //确保桶存在
        ensureBucketExists();
        if (objectName == null||objectName.isEmpty()) {
            objectName = file.getOriginalFilename();
        }
        try(InputStream inputStream = file.getInputStream()){
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream,file.getSize(),-1)// -1 表示未知部分大小，让 MinIO 自动处理
                    .contentType(file.getContentType())//设置content-type
                    .build());
            log.info("文件{}成功上传到bucket{}",objectName,bucketName);
            return objectName;//返回对象名
        } catch (Exception e) {
            log.error("文件上传发生错误：{}",e.getMessage());
            throw e;//由Controller处理
        }
    }
    /**
     * 上传文件流 (另一种方式，例如处理非 MultipartFile 的场景)
     *
     * @param inputStream 输入流
     * @param objectName  对象名称
     * @param contentType 文件类型 (e.g., "image/jpeg")
     * @return 对象名称
     */
    public String uploadFile(InputStream inputStream, String objectName, String contentType) throws Exception {
        ensureBucketExists();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            // 对于未知大小的流，需要指定 partSize (例如 10MB)，这里用 -1 可能导致内存问题，推荐明确大小或使用 known size
                            .stream(inputStream, -1, 10485760) // -1 size, 10MB part size
                            .contentType(contentType)
                            .build()
            );
            log.info("File '{}' uploaded successfully from stream to bucket '{}'.", objectName, bucketName);
            return objectName;
        } catch (Exception e) {
            log.error("Error occurred during stream upload: {}", e.getMessage());
            inputStream.close(); // 确保流被关闭
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    /**
     * 下载文件
     *
     * @param objectName 要下载的对象名称
     * @return 返回文件的 InputStream
     */
    public InputStream downloadFile(String objectName) throws Exception {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error occurred during file download: {}", e.getMessage());
            // 可以根据具体的异常类型 (如 ErrorResponseException 且 code 为 NoSuchKey) 判断文件是否存在
            if (e instanceof ErrorResponseException && ((ErrorResponseException) e).errorResponse().code().equals("NoSuchKey")) {
                log.error("Object '{}' not found in bucket '{}'.", objectName, bucketName);
                // 可以抛出自定义的 NotFound 异常
            }
            throw e;
        }
    }
    /**
     * 生成文件的预签名 URL (用于 GET 请求，即下载)
     *
     * @param objectName 对象名称
     * @param expiryTime 过期时间 (单位：秒)
     * @return 预签名 URL 字符串
     */
    public String generatePresignedGetObjectUrl(String objectName, int expiryTime) throws Exception {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET) // 指定 HTTP 方法为 GET
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiryTime, TimeUnit.HOURS) // 设置过期时间
                            .build()
            );
            log.info("Generated presigned GET URL for '{}': {}", objectName, url);
            return url;
        } catch (Exception e) {
            log.error("Error occurred generating presigned GET URL: {}", e.getMessage());
            throw e;
        }
    }
    /**
     * 生成用于上传文件的预签名 URL (PUT 请求)
     *
     * @param objectName 预期的对象名称
     * @param expiryTime 过期时间 (单位：秒)
     * @return 预签名 URL 字符串
     */
    public String generatePresignedPutObjectUrl(String objectName, int expiryTime) throws Exception {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT) // 指定 HTTP 方法为 PUT
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiryTime, TimeUnit.SECONDS)
                            .build()
            );
            log.info("Generated presigned PUT URL for '{}': {}", objectName, url);
            return url;
        } catch (Exception e) {
            log.error("Error occurred generating presigned PUT URL: {}", e.getMessage());
            throw e;
        }
    }
    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    public void deleteFile(String objectName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("Object '{}' deleted successfully from bucket '{}'.", objectName, bucketName);
        } catch (Exception e) {
            log.error("Error occurred during file deletion: {}", e.getMessage());
            throw e;
        }
    }
}
