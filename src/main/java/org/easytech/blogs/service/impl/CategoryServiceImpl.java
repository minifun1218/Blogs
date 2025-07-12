package org.easytech.blogs.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.Category;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ResourceNotFoundException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.CategoryMapper;
import org.easytech.blogs.mapper.PostMapper;
import org.easytech.blogs.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 分类服务实现类
 * 实现分类相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCategory(Category category) {
        // 参数校验
        if (category == null || !StringUtils.hasText(category.getName())) {
            throw new ValidationException("分类名称不能为空");
        }

        // 检查分类名称是否已存在
        if (isCategoryNameExists(category.getName(), null)) {
            throw new BusinessException("分类名称已存在");
        }

        try {
            // 设置默认值
            if (category.getSortOrder() == null) {
                category.setSortOrder(0);
            }
            if (category.getPostCount() == null) {
                category.setPostCount(0L);
            }

            int result = categoryMapper.insert(category);
            if (result > 0) {
                log.info("分类创建成功，分类名称: {}", category.getName());
                return true;
            }
        } catch (Exception e) {
            log.error("分类创建失败，分类名称: {}", category.getName(), e);
            throw new BusinessException("分类创建失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category createCategory(String name, String description, String icon, Integer sortOrder) {
        // 参数校验
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("分类名称不能为空");
        }

        // 检查分类名称是否已存在
        if (isCategoryNameExists(name, null)) {
            throw new BusinessException("分类名称已存在");
        }

        try {
            // 创建分类对象
            Category category = new Category();
            category.setName(name.trim());
            category.setDescription(description);
            category.setIcon(icon);
            category.setSortOrder(sortOrder != null ? sortOrder : 0);
            category.setPostCount(0L);

            int result = categoryMapper.insert(category);
            if (result > 0) {
                log.info("分类创建成功，分类名称: {}", category.getName());
                return category;
            }
        } catch (Exception e) {
            log.error("分类创建失败，分类名称: {}", name, e);
            throw new BusinessException("分类创建失败，请稍后重试");
        }

        throw new BusinessException("分类创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(Category category) {
        if (category == null || category.getId() == null) {
            throw new ValidationException("分类ID不能为空");
        }

        // 检查分类是否存在
        Category existCategory = categoryMapper.selectById(category.getId());
        if (existCategory == null) {
            throw new ResourceNotFoundException("分类不存在");
        }

        // 检查分类名称是否已存在（排除当前分类）
        if (StringUtils.hasText(category.getName()) && 
            isCategoryNameExists(category.getName(), category.getId())) {
            throw new BusinessException("分类名称已存在");
        }

        try {
            int result = categoryMapper.updateById(category);
            if (result > 0) {
                log.info("分类更新成功，分类ID: {}", category.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("分类更新失败，分类ID: {}", category.getId(), e);
            throw new BusinessException("分类更新失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category updateCategory(Long id, String name, String description, String icon, Integer sortOrder) {
        if (id == null) {
            throw new ValidationException("分类ID不能为空");
        }

        // 检查分类是否存在
        Category existCategory = categoryMapper.selectById(id);
        if (existCategory == null) {
            throw new ResourceNotFoundException("分类不存在");
        }

        // 检查分类名称是否已存在（排除当前分类）
        if (StringUtils.hasText(name) && isCategoryNameExists(name, id)) {
            throw new BusinessException("分类名称已存在");
        }

        try {
            // 更新分类信息
            if (StringUtils.hasText(name)) {
                existCategory.setName(name.trim());
            }
            if (description != null) {
                existCategory.setDescription(description);
            }
            if (icon != null) {
                existCategory.setIcon(icon);
            }
            if (sortOrder != null) {
                existCategory.setSortOrder(sortOrder);
            }

            int result = categoryMapper.updateById(existCategory);
            if (result > 0) {
                log.info("分类更新成功，分类ID: {}", existCategory.getId());
                return existCategory;
            }
        } catch (Exception e) {
            log.error("分类更新失败，分类ID: {}", id, e);
            throw new BusinessException("分类更新失败，请稍后重试");
        }

        throw new BusinessException("分类更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long categoryId) {
        if (categoryId == null) {
            throw new ValidationException("分类ID不能为空");
        }

        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new ResourceNotFoundException("分类不存在");
        }

        // 检查是否有文章使用该分类
        Long postCount = postMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<org.easytech.blogs.entity.Post>()
                .eq("category_id", categoryId)
                .eq("is_deleted", 0)
        );

        if (postCount > 0) {
            throw new BusinessException("该分类下还有文章，不能删除");
        }

        try {
            int result = categoryMapper.deleteById(categoryId);
            if (result > 0) {
                log.info("分类删除成功，分类ID: {}", categoryId);
                return true;
            }
        } catch (Exception e) {
            log.error("分类删除失败，分类ID: {}", categoryId, e);
            throw new BusinessException("分类删除失败，请稍后重试");
        }

        return false;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryMapper.selectById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAllCategories();
    }

    @Override
    public Category getCategoryByName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return categoryMapper.findByName(name);
    }

    @Override
    public List<Category> getCategoriesWithPosts() {
        return categoryMapper.selectCategoriesWithPosts();
    }

    @Override
    public boolean isCategoryNameExists(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }

        Category existCategory = categoryMapper.findByName(name);
        if (existCategory == null) {
            return false;
        }

        // 如果是更新操作，排除当前分类
        if (excludeId != null && existCategory.getId().equals(excludeId)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean updateCategoryPostCount(Long categoryId) {
        if (categoryId == null) {
            return false;
        }

        try {
            // 统计该分类下已发布的文章数量
            Long postCount = postMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<org.easytech.blogs.entity.Post>()
                    .eq("category_id", categoryId)
                    .eq("status", 1) // 已发布状态
                    .eq("is_deleted", 0)
            );

            int result = categoryMapper.updatePostCount(categoryId, postCount);
            if (result > 0) {
                log.debug("分类文章数量更新成功，分类ID: {}, 文章数量: {}", categoryId, postCount);
                return true;
            }
        } catch (Exception e) {
            log.error("分类文章数量更新失败，分类ID: {}", categoryId, e);
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllCategoryPostCounts() {
        try {
            List<Category> categories = categoryMapper.selectAllCategories();
            int successCount = 0;

            for (Category category : categories) {
                if (updateCategoryPostCount(category.getId())) {
                    successCount++;
                }
            }

            log.info("批量更新分类文章数量完成，成功更新: {}/{}", successCount, categories.size());
            return successCount == categories.size();
        } catch (Exception e) {
            log.error("批量更新分类文章数量失败", e);
            throw new BusinessException("批量更新分类文章数量失败");
        }
    }
}