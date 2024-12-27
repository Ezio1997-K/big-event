package com.bootzero.big_event.controllers;

import com.bootzero.big_event.bean.Category;
import com.bootzero.big_event.bean.Result;
import com.bootzero.big_event.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: CategoryController
 * Package: com.bootzero.big_event.controllers
 * Description:
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping
    public Result<Void> add(@RequestBody @Validated(Category.Add.class) Category category){
        categoryService.add(category);
        return Result.success();
    }
    @GetMapping
    public Result<List<Category>> list(){
        List<Category> cs = categoryService.list();
        return Result.success(cs);
    }
    @GetMapping("/detail")
    public Result<Category> detail(Integer id){
        Category c = categoryService.findById(id);
        return Result.success(c);
    }
    @PutMapping
    public Result<Void> update(@RequestBody @Validated(Category.Update.class) Category category){
        categoryService.update(category);
        return Result.success();
    }
    @DeleteMapping
    public Result<Void> delete(@RequestParam("id") Integer id){
        categoryService.deleteById(id);
        return Result.success();
    }
}
