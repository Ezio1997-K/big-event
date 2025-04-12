package com.bootzero.big_event.config;

import lombok.Getter; // 使用 @Getter 更方便
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner; // 引入 S3Presigner

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "myminio")
@Getter // 使用 @Getter 代替 @Setter，或者两者都用（如果需要在运行时修改）
@Setter // 保持 @Setter 以便 Spring 可以注入属性
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    // MinIO 通常不需要 bucket name 在这里，可以在 application.properties 定义，然后在 Util 中使用 @Value
    // private String bucketName;
    private String region = "us-east-1"; // 对于 MinIO，区域通常不重要，但 SDK 需要一个

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // 对 MinIO 很重要
                        .chunkedEncodingEnabled(false) // 对于某些 MinIO/代理组合可能需要
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        // S3Presigner 使用与 S3Client 相同的配置
        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // 对 MinIO 很重要
                        .chunkedEncodingEnabled(false) // 保持一致
                        .build())
                .build();
    }
}
