package com.bootzero.big_event.controllers;

import com.bootzero.big_event.bean.Result;
import com.bootzero.big_event.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ClassName: ArticleController
 * Package: com.bootzero.big_event.controllers
 * Description:
 *
 */
@RestController
@RequestMapping("/article")
public class ArticleController {
    @GetMapping("/list")
    public Result<?> list(@RequestHeader("Authorization") String token, HttpServletResponse response){
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            return Result.success("所有文章数据。。。");
        } catch (Exception e) {
            response.setStatus(401);
            return Result.error("未登录");
        }
    }
}
