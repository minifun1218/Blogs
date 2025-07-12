package org.easytech.blogs.controller;

import org.easytech.blogs.common.Result;
import org.easytech.blogs.entity.Category;
import org.easytech.blogs.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 创建分类（管理员功能）
     */
    @PostMapping
    public Result<Category> createCategory(@RequestBody CreateCategoryRequest request) {
        try {
            Category category = categoryService.createCategory(
                request.getName(),
                request.getDescription(),
                request.getIcon(),
                request.getSortOrder()
            );
            return Result.success("分类创建成功", category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新分类（管理员功能）
     */
    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody UpdateCategoryRequest request) {
        try {
            Category category = categoryService.updateCategory(
                id,
                request.getName(),
                request.getDescription(),
                request.getIcon(),
                request.getSortOrder()
            );
            return Result.success("分类更新成功", category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除分类（管理员功能）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        try {
            boolean success = categoryService.deleteCategory(id);
            return success ? Result.success("删除成功", null) : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return Result.notFound();
        }
        return Result.success(category);
    }

    /**
     * 查询所有分类
     */
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 获取有文章的分类
     */
    @GetMapping("/with-posts")
    public Result<List<Category>> getCategoriesWithPosts() {
        List<Category> categories = categoryService.getCategoriesWithPosts();
        return Result.success(categories);
    }

    /**
     * 根据名称查询分类
     */
    @GetMapping("/name/{name}")
    public Result<Category> getCategoryByName(@PathVariable String name) {
        Category category = categoryService.findByName(name);
        if (category == null) {
            return Result.notFound();
        }
        return Result.success(category);
    }

    /**
     * 检查分类名称是否存在
     */
    @GetMapping("/check-name")
    public Result<Boolean> checkCategoryName(@RequestParam String name) {
        boolean exists = categoryService.existsByName(name);
        return Result.success(exists);
    }

    /**
     * 更新分类文章数量（管理员功能）
     */
    @PutMapping("/{id}/update-post-count")
    public Result<Void> updatePostCount(@PathVariable Long id) {
        boolean success = categoryService.updatePostCount(id);
        return success ? Result.success("更新成功", null) : Result.error("更新失败");
    }

    // 请求参数类
    public static class CreateCategoryRequest {
        private String name;
        private String description;
        private String icon;
        private Integer sortOrder;

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }

    public static class UpdateCategoryRequest {
        private String name;
        private String description;
        private String icon;
        private Integer sortOrder;

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }
}
