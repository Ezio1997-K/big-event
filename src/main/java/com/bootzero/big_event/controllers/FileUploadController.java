package com.bootzero.big_event.controllers;

import com.aliyuncs.exceptions.ClientException;
import com.bootzero.big_event.bean.Result;
import com.bootzero.big_event.utils.AliOssUtil;
import com.bootzero.big_event.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * ClassName: FileUploadController
 * Package: com.bootzero.big_event.controllers
 * Description:
 *
 */
@Slf4j
@RestController
public class FileUploadController {
    @Autowired
    private MinioUtil minioUtil;
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file")MultipartFile file) throws IOException, ClientException {
        /*//保证文件的名字是唯一的,从而防止文件覆盖
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
        String url = AliOssUtil.uploadFile(filename, file.getInputStream());
        return Result.success(url);*/
        System.out.println("Controller received file: " + file.getOriginalFilename() + ", size: " + file.getSize());
        // ---- 临时调试代码 开始 ----
        try (InputStream testStream = file.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead = testStream.read(buffer);
            if (bytesRead == -1) {
                System.out.println("!!!!!!!!!! InputStream is EMPTY at Controller entry !!!!!!!!!!");
            } else {
                System.out.println("Successfully read " + bytesRead + " bytes from InputStream at Controller entry.");
            }
        } catch (IOException e) {
            System.err.println("Error reading InputStream at Controller entry: " + e.getMessage());
        }
        // ---- 临时调试代码 结束 ----
        if(file.isEmpty())
            throw new RuntimeException("上传文件不能为空！");
        String originalFilename = file.getOriginalFilename();
        //生成唯一文件名
        String filename = UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
        //String

        String contentType =file.getContentType();
        try{
            String url =  minioUtil.uploadFile("big-event",filename,file.getInputStream(),contentType,file.getSize());
            return Result.success(url);
        }catch (IOException e){
            throw new RuntimeException("获取文件流失败",e);
        }
    }
}

