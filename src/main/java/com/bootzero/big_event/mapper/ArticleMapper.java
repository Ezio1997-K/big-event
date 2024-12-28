package com.bootzero.big_event.mapper;

import com.bootzero.big_event.bean.Article;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ClassName: ArticleMapper
 * Package: com.bootzero.big_event.mapper
 * Description:
 *
 */
@Mapper
public interface ArticleMapper {
    void add(Article article);

    Page<Article> list(@Param("categoryId") Integer categoryId,@Param("status") String status,@Param("id") Integer id);

    Article detail(Integer id, Integer userId);
}
