package com.bootzero.big_event.utils;

import com.bootzero.big_event.config.MinioConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration; // 使用 Duration 替代 Date

@Component
@RequiredArgsConstructor // Lombok: 自动生成包含 final 字段的构造函数，用于注入
@Slf4j // Lombok: 添加日志记录器
public class MinioUtil {

    private final S3Client s3Client; // 通过构造函数注入
    private final S3Presigner s3Presigner; // 通过构造函数注入
    private final MinioConfig minioConfig; // 注入配置以获取 endpoint

    // 可以将默认 Bucket 配置在 application.properties 中
    // myminio.bucket-name=your-default-bucket
    @Value("${myminio.bucket-name:default-bucket}") // :default-bucket 是默认值
    private String defaultBucketName;

    /**
     * 上传文件流到 MinIO/S3
     *
     * @param bucketName  存储桶名称
     * @param objectName  对象名称 (包含路径，例如: images/avatar.jpg)
     * @param inputStream 文件输入流
     * @param contentType 文件类型 (例如: "image/jpeg", "application/pdf")
     * @return 上传成功后对象的 URL，如果失败则返回 null
     */
    public String uploadFile(String bucketName, String objectName, InputStream inputStream, String contentType,long length) {
        try {
            // SDK v2 需要知道 InputStream 的大小，否则可能出错或效率低下
            // 注意：inputStream.available() 不总是可靠，特别是网络流
            // 如果可能，最好传入 byte[] 或 File，或者预先知道流的大小
            // 这里我们尝试使用 available()，但在生产环境中需要谨慎
            //long contentLength = inputStream.available();
            long contentLength = length;
            if (contentLength < 0) {
                // 如果无法获取大小，可以考虑先读取到内存或临时文件，但这会增加资源消耗
                log.warn("无法获取 InputStream 的大小，上传可能失败或效率低下。Object: {}/{}", bucketName, objectName);
                // 或者抛出异常，强制调用者提供大小或使用其他方法
                // throw new IllegalArgumentException("无法确定 InputStream 的大小");
                // 如果 S3Client 配置允许不带 Content-Length 上传（不推荐，且通常需要 chunked encoding），则可以继续
                // 但 MinIO + pathStyle + 某些代理可能不支持 chunked encoding，所以上面配置禁用了它
            }


            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .contentType(contentType) // 设置 Content-Type 很重要
                    .contentLength(contentLength) // 如果能确定长度，设置它
                    .build();

            // 使用 RequestBody.fromInputStream
            // 如果 contentLength > 0 或已知，应传入：RequestBody.fromInputStream(inputStream, contentLength)
            // 如果不确定长度且允许无长度上传（需 S3Client 配置支持），可尝试不传长度，但这里我们传入已获取（可能不准）的长度
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, contentLength);

            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);
            log.info("文件上传成功 ETag: {}, Bucket: {}, Object: {}", response.eTag(), bucketName, objectName);

            // 构建并返回文件的访问 URL (需要确保 bucket 是公开可读的，或者之后使用预签名 URL 访问)
            // 注意：这里的 URL 构造方式依赖于 endpoint 和 path-style 访问
            return String.format("%s/%s/%s", minioConfig.getEndpoint(), bucketName, objectName);

        } catch (S3Exception e) {
            log.error("上传文件到 MinIO 时 S3 错误, Bucket: {}, Object: {}: {}", bucketName, objectName, e.getMessage(), e);
            // 可以根据 e.statusCode() 等进行更细致的处理
        } catch (SdkClientException e) {
            log.error("上传文件到 MinIO 时客户端连接或配置错误, Bucket: {}, Object: {}: {}", bucketName, objectName, e.getMessage(), e);
        } /*catch (IOException e) {
            log.error("读取上传文件流时发生 IO 错误, Bucket: {}, Object: {}: {}", bucketName, objectName, e.getMessage(), e);
        }*/ finally {
            // 确保 InputStream 被关闭
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭 InputStream 时出错", e);
                }
            }
        }
        return null; // 上传失败
    }

    /**
     * 使用默认 Bucket 上传文件
     */
    /*public String uploadFile(String objectName, InputStream inputStream, String contentType) {
        return uploadFile(defaultBucketName, objectName, inputStream, contentType);
    }*/


    /**
     * 生成一个用于上传文件（PUT）的预签名 URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param duration   URL 有效期
     * @return 预签名 URL 字符串，如果生成失败则返回 null
     */
    public String generatePresignedPutUrl(String bucketName, String objectName, Duration duration) {
        try {
            // 1. 创建基础的 PutObjectRequest (只需 bucket 和 key)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    // 如果需要在上传时强制指定 Content-Type，可以在这里添加 .contentType("...")
                    // 但通常由客户端上传时在 Header 中指定
                    .build();

            // 2. 创建 PutObjectPresignRequest
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(duration) // 设置有效期
                    .putObjectRequest(putObjectRequest) // 关联 PutObjectRequest
                    .build();

            // 3. 使用 S3Presigner 生成预签名请求对象
            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);

            // 4. 获取 URL
            URL url = presignedPutObjectRequest.url();
            log.info("成功生成预签名 PUT URL: {}, 有效期: {}", url, duration);
            return url.toString();

        } catch (S3Exception e) {
            log.error("生成预签名 PUT URL 时 S3 错误, Bucket: {}, Object: {}: {}", bucketName, objectName, e.getMessage(), e);
        } catch (SdkClientException e) {
            log.error("生成预签名 PUT URL 时客户端连接或配置错误, Bucket: {}, Object: {}: {}", bucketName, objectName, e.getMessage(), e);
        }
        return null; // 生成失败
    }

    /**
     * 使用默认 Bucket 生成预签名 PUT URL
     */
    public String generatePresignedPutUrl(String objectName, Duration duration) {
        return generatePresignedPutUrl(defaultBucketName, objectName, duration);
    }


    // --- 你可以根据需要添加其他方法 ---
    // 例如：下载文件、删除文件、生成预签名 GET URL 等

    /**
     * 生成一个用于下载文件（GET）的预签名 URL
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param duration   URL 有效期
     * @return 预签名 URL 字符串，如果生成失败则返回 null
     */
//    public String generatePresignedGetUrl(String bucketName, String objectName, Duration duration) {
//        try {
//            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(objectName)
//                    .build();
//
//            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                    .signatureDuration(duration)
//                    .getObjectRequest(getObjectRequest)
//                    .build();
//
//            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
//            URL url = presignedGetObjectRequest.url();
//            log.info("成功生成预签名 GET URL: {}, 有效期: {}", url, duration);
//            return url.toString();
//
//        } catch (S3Exception e) {
//            log.error("生成预签名 GET URL 时 S3 错误, Bucket: {}, Object: {}: {}", bucketName, objectName, e.getMessage(), e);
//        } catch (SdkClientException e) {
//            log.error("生成预签名 GET URL 时客户端连接或配置错误, Bucket: {}, Object: {}: {}", bucketName, objectName, e.getMessage(), e);
//        }
//        return null;
//    }

}
