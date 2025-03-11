package com.bootzero.big_event.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * ClassName: AliOssUtil
 * Package: com.bootzero.big_event.utils
 * Description:
 */
public final class AliOssUtil {
    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    private static final String ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";
    // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
    //private static final EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
    // 填写Bucket名称，例如examplebucket。
    private static final String BUCKETNAME = "big-eventggg";
    // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
    //String objectName = "exampledir/exampleobject.txt";
    // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
    private static final String REGION = "cn-beijing";

    private AliOssUtil() {
        throw new RuntimeException("工具类不能被实例化");
    }

    //上传文件
    public static String uploadFile(String objectName, InputStream inputStream) throws ClientException {
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 创建OSSClient实例。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(ENDPOINT)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(REGION)
                .build();
        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKETNAME, objectName, inputStream);

        // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        // metadata.setObjectAcl(CannedAccessControlList.Private);
        // putObjectRequest.setMetadata(metadata);

        // 上传字符串。
        PutObjectResult result = ossClient.putObject(putObjectRequest);
        return getSignedUrl(objectName);
    }

    private static String getSignedUrl(String objectName) throws ClientException {
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Object完整路径，例如exampleobject.txt。Object完整路径中不能包含Bucket名称。
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, credentialsProvider);

        URL signedUrl = null;

        // 指定生成的签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000L * 24 * 7);//实际7天

        // 生成签名URL。
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKETNAME, objectName, HttpMethod.GET);
        // 设置过期时间。
        request.setExpiration(expiration);

        // 通过HTTP GET请求生成签名URL。
        signedUrl = ossClient.generatePresignedUrl(request);
        return signedUrl.toExternalForm();
        /*// 打印签名URL。
        System.out.println("signed url for getObject: " + signedUrl);
        System.out.println("-----------------------");*/
        //System.out.println(URLDecoder.decode(signedUrl.toString(), StandardCharsets.UTF_8));
    }
}

