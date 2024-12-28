package com.bootzero.big_event.service;

import com.bootzero.big_event.bean.Article;
import com.bootzero.big_event.bean.PageBean;

/**
 * ClassName: ArticleService
 * Package: com.bootzero.big_event.service
 * Description:
 *
 */
public interface ArticleService {
    //新增文章
    void add(Article article);
    //获取文章列表
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String status);
    //查看文章详情
    Article detail(Integer id);
}
