package com.bootzero.big_event.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;


import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "myminio")
@Setter
public class MinioConfig {
    // 从配置文件或环境变量获取更佳
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region = "us-east-1"; // 对于 MinIO，区域通常不重要，但 SDK 需要一个
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region)) // 设置区域
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                // 重要：对于 MinIO，通常需要启用路径样式访问
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
