package com.bootzero.big_event.mapper;

import com.bootzero.big_event.bean.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: CategoryMapper
 * Package: com.bootzero.big_event.mapper
 * Description:
 *
 */
@Mapper
public interface CategoryMapper {

    void add(Category category);

    List<Category> list(Integer id);

    Category findById(Integer id);

    void update(Category category);

    void deleteById(Integer id);
}
