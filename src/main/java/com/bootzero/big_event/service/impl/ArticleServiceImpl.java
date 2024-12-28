package com.bootzero.big_event.service.impl;

import com.bootzero.big_event.bean.Article;
import com.bootzero.big_event.bean.PageBean;
import com.bootzero.big_event.mapper.ArticleMapper;
import com.bootzero.big_event.service.ArticleService;
import com.bootzero.big_event.utils.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ArticleServiceImpl
 * Package: com.bootzero.big_event.service.impl
 * Description:
 */
@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;

    @Override
    public void add(Article article) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        article.setCreateUser(id);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.add(article);
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String status) {
        PageBean<Article> pageBean = new PageBean<>();
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        //开启分页
        try (Page<Object> objects = PageHelper.startPage(pageNum, pageSize)) {
            Page<Article> articles = articleMapper.list(categoryId, status, id);
            pageBean.setTotal(articles.getTotal());
            pageBean.setItems(articles.getResult());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return pageBean;
    }

    @Override
    public Article detail(Integer id) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        return articleMapper.detail(id,userId);
    }
}
