package com.bootzero.big_event.controllers;

import com.aliyuncs.exceptions.ClientException;
import com.bootzero.big_event.bean.Result;
import com.bootzero.big_event.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file")MultipartFile file) throws IOException, ClientException {
        //保证文件的名字是唯一的,从而防止文件覆盖
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
        String url = AliOssUtil.uploadFile(filename, file.getInputStream());
        return Result.success(url);
    }
}
