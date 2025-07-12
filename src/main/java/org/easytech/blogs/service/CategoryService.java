package org.easytech.blogs.service;

import org.easytech.blogs.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 * 提供分类相关的业务逻辑处理
 */
public interface CategoryService {

    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建结果
     */
    boolean createCategory(Category category);

    /**
     * 创建分类（重载方法）
     * @param name 分类名称
     * @param description 分类描述
     * @param icon 分类图标
     * @param sortOrder 排序权重
     * @return 创建的分类对象
     */
    Category createCategory(String name, String description, String icon, Integer sortOrder);

    /**
     * 更新分类
     * @param category 分类信息
     * @return 更新结果
     */
    boolean updateCategory(Category category);

    /**
     * 更新分类（重载方法）
     * @param id 分类ID
     * @param name 分类名称
     * @param description 分类描述
     * @param icon 分类图标
     * @param sortOrder 排序权重
     * @return 更新的分类对象
     */
    Category updateCategory(Long id, String name, String description, String icon, Integer sortOrder);

    /**
     * 删除分类
     * @param categoryId 分类ID
     * @return 删除结果
     */
    boolean deleteCategory(Long categoryId);

    /**
     * 根据ID获取分类
     * @param categoryId 分类ID
     * @return 分类信息
     */
    Category getCategoryById(Long categoryId);

    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();

    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类信息
     */
    Category getCategoryByName(String name);

    /**
     * 获取有文章的分类
     * @return 有文章的分类列表
     */
    List<Category> getCategoriesWithPosts();

    /**
     * 检查分类名称是否存在
     * @param name 分类名称
     * @param excludeId 排除的分类ID
     * @return 是否存在
     */
    boolean isCategoryNameExists(String name, Long excludeId);

    /**
     * 更新分类文章数量
     * @param categoryId 分类ID
     * @return 更新结果
     */
    boolean updateCategoryPostCount(Long categoryId);

    /**
     * 批量更新所有分类的文章数量
     * @return 更新结果
     */
    boolean updateAllCategoryPostCounts();

    /**
     * 根据名称查询分类（别名方法）
     * @param name 分类名称
     * @return 分类信息
     */
    default Category findByName(String name) {
        return getCategoryByName(name);
    }

    /**
     * 根据ID获取分类（别名方法）
     * @param categoryId 分类ID
     * @return 分类信息
     */
    default Category getById(Long categoryId) {
        return getCategoryById(categoryId);
    }

    /**
     * 检查分类名称是否存在（别名方法）
     * @param name 分类名称
     * @return 是否存在
     */
    default boolean existsByName(String name) {
        return isCategoryNameExists(name, null);
    }

    /**
     * 更新分类文章数量（别名方法）
     * @param categoryId 分类ID
     * @return 更新结果
     */
    default boolean updatePostCount(Long categoryId) {
        return updateCategoryPostCount(categoryId);
    }
}