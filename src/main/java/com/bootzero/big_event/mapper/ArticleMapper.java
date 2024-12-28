package com.bootzero.big_event.mapper;

import com.bootzero.big_event.bean.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: ArticleMapper
 * Package: com.bootzero.big_event.mapper
 * Description:
 *
 */
@Mapper
public interface ArticleMapper {
    void add(Article article);
}
