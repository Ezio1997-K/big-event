package com.bootzero.big_event.service;

import com.bootzero.big_event.bean.Category;

import java.util.List;

/**
 * ClassName: CategoryService
 * Package: com.bootzero.big_event.service
 * Description:
 *
 */
public interface CategoryService {
    //添加文章分类
    void add(Category category);
    //列表查询
    List<Category> list();
    //查询详情
    Category findById(Integer id);
    //修改分类
    void update(Category category);

    void deleteById(Integer id);
}
