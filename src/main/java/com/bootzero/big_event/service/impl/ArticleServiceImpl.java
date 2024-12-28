package com.bootzero.big_event.service.impl;

import com.bootzero.big_event.bean.Article;
import com.bootzero.big_event.mapper.ArticleMapper;
import com.bootzero.big_event.service.ArticleService;
import com.bootzero.big_event.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ClassName: ArticleServiceImpl
 * Package: com.bootzero.big_event.service.impl
 * Description:
 *
 */
@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;

    @Override
    public void add(Article article) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        article.setCreateUser(id);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.add(article);
    }
}
