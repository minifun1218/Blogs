package org.easytech.blogs.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.Tag;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ResourceNotFoundException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.PostTagMapper;
import org.easytech.blogs.mapper.TagMapper;
import org.easytech.blogs.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签服务实现类
 * 实现标签相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTag(Tag tag) {
        // 参数校验
        if (tag == null || !StringUtils.hasText(tag.getName())) {
            throw new ValidationException("标签名称不能为空");
        }

        // 检查标签名称是否已存在
        if (isTagNameExists(tag.getName(), null)) {
            throw new BusinessException("标签名称已存在");
        }

        try {
            // 设置默认值
            if (tag.getUseCount() == null) {
                tag.setUseCount(0L);
            }

            int result = tagMapper.insert(tag);
            if (result > 0) {
                log.info("标签创建成功，标签名称: {}", tag.getName());
                return true;
            }
        } catch (Exception e) {
            log.error("标签创建失败，标签名称: {}", tag.getName(), e);
            throw new BusinessException("标签创建失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tag createTag(String name, String color) {
        // 参数校验
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("标签名称不能为空");
        }

        // 检查标签名称是否已存在
        if (isTagNameExists(name, null)) {
            throw new BusinessException("标签名称已存在");
        }

        try {
            // 创建标签对象
            Tag tag = new Tag();
            tag.setName(name.trim());
            tag.setColor(color);
            tag.setUseCount(0L);

            int result = tagMapper.insert(tag);
            if (result > 0) {
                log.info("标签创建成功，标签名称: {}", tag.getName());
                return tag;
            }
        } catch (Exception e) {
            log.error("标签创建失败，标签名称: {}", name, e);
            throw new BusinessException("标签创建失败，请稍后重试");
        }

        throw new BusinessException("标签创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTag(Tag tag) {
        if (tag == null || tag.getId() == null) {
            throw new ValidationException("标签ID不能为空");
        }

        // 检查标签是否存在
        Tag existTag = tagMapper.selectById(tag.getId());
        if (existTag == null) {
            throw new ResourceNotFoundException("标签不存在");
        }

        // 检查标签名称是否已存在（排除当前标签）
        if (StringUtils.hasText(tag.getName()) && 
            isTagNameExists(tag.getName(), tag.getId())) {
            throw new BusinessException("标签名称已存在");
        }

        try {
            int result = tagMapper.updateById(tag);
            if (result > 0) {
                log.info("标签更新成功，标签ID: {}", tag.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("标签更新失败，标签ID: {}", tag.getId(), e);
            throw new BusinessException("标签更新失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tag updateTag(Long id, String name, String color) {
        if (id == null) {
            throw new ValidationException("标签ID不能为空");
        }

        // 检查标签是否存在
        Tag existTag = tagMapper.selectById(id);
        if (existTag == null) {
            throw new ResourceNotFoundException("标签不存在");
        }

        // 检查标签名称是否已存在（排除当前标签）
        if (StringUtils.hasText(name) && isTagNameExists(name, id)) {
            throw new BusinessException("标签名称已存在");
        }

        try {
            // 更新标签信息
            if (StringUtils.hasText(name)) {
                existTag.setName(name.trim());
            }
            if (color != null) {
                existTag.setColor(color);
            }

            int result = tagMapper.updateById(existTag);
            if (result > 0) {
                log.info("标签更新成功，标签ID: {}", existTag.getId());
                return existTag;
            }
        } catch (Exception e) {
            log.error("标签更新失败，标签ID: {}", id, e);
            throw new BusinessException("标签更新失败，请稍后重试");
        }

        throw new BusinessException("标签更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTag(Long tagId) {
        if (tagId == null) {
            throw new ValidationException("标签ID不能为空");
        }

        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new ResourceNotFoundException("标签不存在");
        }

        // 检查是否有文章使用该标签
        Long useCount = postTagMapper.countByTagId(tagId);
        if (useCount > 0) {
            throw new BusinessException("该标签下还有文章，不能删除");
        }

        try {
            int result = tagMapper.deleteById(tagId);
            if (result > 0) {
                log.info("标签删除成功，标签ID: {}", tagId);
                return true;
            }
        } catch (Exception e) {
            log.error("标签删除失败，标签ID: {}", tagId, e);
            throw new BusinessException("标签删除失败，请稍后重试");
        }

        return false;
    }

    @Override
    public Tag getTagById(Long tagId) {
        if (tagId == null) {
            return null;
        }
        return tagMapper.selectById(tagId);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagMapper.selectAllTags();
    }

    @Override
    public Tag getTagByName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return tagMapper.findByName(name);
    }

    @Override
    public List<Tag> getTagsByPostId(Long postId) {
        if (postId == null) {
            return List.of();
        }
        return tagMapper.selectTagsByPostId(postId);
    }

    @Override
    public List<Tag> getHotTags(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        return tagMapper.selectHotTags(limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Tag> getOrCreateTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return List.of();
        }

        List<Tag> tags = new ArrayList<>();
        
        for (String tagName : tagNames) {
            if (StringUtils.hasText(tagName)) {
                String trimmedName = tagName.trim();
                
                // 先查找已存在的标签
                Tag existTag = tagMapper.findByName(trimmedName);
                if (existTag != null) {
                    tags.add(existTag);
                } else {
                    // 创建新标签
                    Tag newTag = new Tag();
                    newTag.setName(trimmedName);
                    newTag.setUseCount(0L);
                    
                    try {
                        tagMapper.insert(newTag);
                        tags.add(newTag);
                        log.info("创建新标签: {}", trimmedName);
                    } catch (Exception e) {
                        log.error("创建标签失败: {}", trimmedName, e);
                        // 继续处理其他标签，不抛出异常
                    }
                }
            }
        }

        return tags;
    }

    @Override
    public boolean isTagNameExists(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }

        Tag existTag = tagMapper.findByName(name);
        if (existTag == null) {
            return false;
        }

        // 如果是更新操作，排除当前标签
        if (excludeId != null && existTag.getId().equals(excludeId)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean updateTagUseCount(Long tagId) {
        if (tagId == null) {
            return false;
        }

        try {
            // 统计标签使用次数
            Long useCount = postTagMapper.countByTagId(tagId);
            
            int result = tagMapper.updateUseCount(tagId, useCount);
            if (result > 0) {
                log.debug("标签使用次数更新成功，标签ID: {}, 使用次数: {}", tagId, useCount);
                return true;
            }
        } catch (Exception e) {
            log.error("标签使用次数更新失败，标签ID: {}", tagId, e);
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllTagUseCounts() {
        try {
            List<Tag> tags = tagMapper.selectAllTags();
            int successCount = 0;

            for (Tag tag : tags) {
                if (updateTagUseCount(tag.getId())) {
                    successCount++;
                }
            }

            log.info("批量更新标签使用次数完成，成功更新: {}/{}", successCount, tags.size());
            return successCount == tags.size();
        } catch (Exception e) {
            log.error("批量更新标签使用次数失败", e);
            throw new BusinessException("批量更新标签使用次数失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanUnusedTags() {
        try {
            // 查找使用次数为0的标签
            List<Tag> unusedTags = tagMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Tag>()
                    .eq("use_count", 0)
                    .eq("is_deleted", 0)
            );

            int deletedCount = 0;
            for (Tag tag : unusedTags) {
                // 再次确认标签确实未被使用
                Long actualUseCount = postTagMapper.countByTagId(tag.getId());
                if (actualUseCount == 0) {
                    tagMapper.deleteById(tag.getId());
                    deletedCount++;
                    log.info("清理未使用标签: {}", tag.getName());
                } else {
                    // 如果实际使用次数不为0，更新使用次数
                    tagMapper.updateUseCount(tag.getId(), actualUseCount);
                    log.info("修正标签使用次数: {}, 实际次数: {}", tag.getName(), actualUseCount);
                }
            }

            log.info("清理未使用标签完成，共清理: {} 个", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("清理未使用标签失败", e);
            throw new BusinessException("清理未使用标签失败");
        }
    }
}