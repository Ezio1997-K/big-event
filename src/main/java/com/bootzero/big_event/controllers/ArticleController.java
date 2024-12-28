package com.bootzero.big_event.controllers;

import com.bootzero.big_event.bean.Article;
import com.bootzero.big_event.bean.PageBean;
import com.bootzero.big_event.bean.Result;
import com.bootzero.big_event.service.ArticleService;
import com.bootzero.big_event.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ClassName: ArticleController
 * Package: com.bootzero.big_event.controllers
 * Description:
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;

    /*@GetMapping("/list")
    public Result<?> list(*//*@RequestHeader("Authorization") String token, HttpServletResponse response*//*){
     *//*try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            return Result.success("所有文章数据。。。");
        } catch (Exception e) {
            response.setStatus(401);
            return Result.error("未登录");
        }*//*
        return Result.success("所有文章数据。。。");
    }*/
    @PostMapping
    public Result<Void> add(@RequestBody @Validated Article article) {
        articleService.add(article);
        return Result.success();
    }
    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String status
    ) {
       PageBean<Article> pb = articleService.list(pageNum,pageSize,categoryId,status);
       return Result.success(pb);
    }
}
