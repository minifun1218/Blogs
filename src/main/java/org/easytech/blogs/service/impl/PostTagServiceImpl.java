package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.PostTag;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.PostMapper;
import org.easytech.blogs.mapper.PostTagMapper;
import org.easytech.blogs.mapper.TagMapper;
import org.easytech.blogs.service.PostTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文章标签关联服务实现类
 * 实现文章标签关联相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostTagServiceImpl implements PostTagService {

    private final PostTagMapper postTagMapper;
    private final PostMapper postMapper;
    private final TagMapper tagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPostTag(Long postId, Long tagId) {
        if (postId == null || tagId == null) {
            throw new ValidationException("文章ID和标签ID不能为空");
        }

        // 检查是否已存在关联
        if (hasPostTag(postId, tagId)) {
            return true; // 已存在关联
        }

        try {
            PostTag postTag = new PostTag();
            postTag.setPostId(postId);
            postTag.setTagId(tagId);

            int result = postTagMapper.insert(postTag);
            if (result > 0) {
                // 更新标签使用次数
                updateTagUseCount(tagId);
                log.info("文章标签关联添加成功，文章ID: {}, 标签ID: {}", postId, tagId);
                return true;
            }
        } catch (Exception e) {
            log.error("文章标签关联添加失败，文章ID: {}, 标签ID: {}", postId, tagId, e);
            throw new BusinessException("文章标签关联添加失败");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPostTags(Long postId, List<Long> tagIds) {
        if (postId == null || tagIds == null || tagIds.isEmpty()) {
            throw new ValidationException("文章ID和标签ID列表不能为空");
        }

        try {
            int successCount = 0;
            for (Long tagId : tagIds) {
                if (addPostTag(postId, tagId)) {
                    successCount++;
                }
            }

            log.info("批量添加文章标签关联完成，文章ID: {}, 成功添加: {}/{}", postId, successCount, tagIds.size());
            return successCount > 0;
        } catch (Exception e) {
            log.error("批量添加文章标签关联失败，文章ID: {}", postId, e);
            throw new BusinessException("批量添加文章标签关联失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setPostTags(Long postId, List<Long> tagIds) {
        if (postId == null) {
            throw new ValidationException("文章ID不能为空");
        }

        try {
            // 先删除原有关联
            removeAllPostTags(postId);

            // 添加新关联
            if (tagIds != null && !tagIds.isEmpty()) {
                return addPostTags(postId, tagIds);
            }

            return true;
        } catch (Exception e) {
            log.error("设置文章标签关联失败，文章ID: {}", postId, e);
            throw new BusinessException("设置文章标签关联失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removePostTag(Long postId, Long tagId) {
        if (postId == null || tagId == null) {
            throw new ValidationException("文章ID和标签ID不能为空");
        }

        try {
            QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("post_id", postId).eq("tag_id", tagId);

            int result = postTagMapper.delete(queryWrapper);
            if (result > 0) {
                // 更新标签使用次数
                updateTagUseCount(tagId);
                log.info("文章标签关联删除成功，文章ID: {}, 标签ID: {}", postId, tagId);
                return true;
            }
        } catch (Exception e) {
            log.error("文章标签关联删除失败，文章ID: {}, 标签ID: {}", postId, tagId, e);
            throw new BusinessException("文章标签关联删除失败");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllPostTags(Long postId) {
        if (postId == null) {
            throw new ValidationException("文章ID不能为空");
        }

        try {
            // 获取要删除的标签ID列表，用于更新使用次数
            List<Long> tagIds = getTagIdsByPostId(postId);

            int result = postTagMapper.deleteByPostId(postId);
            
            // 更新相关标签的使用次数
            for (Long tagId : tagIds) {
                updateTagUseCount(tagId);
            }

            log.info("文章所有标签关联删除成功，文章ID: {}, 删除数量: {}", postId, result);
            return true;
        } catch (Exception e) {
            log.error("文章所有标签关联删除失败，文章ID: {}", postId, e);
            throw new BusinessException("文章所有标签关联删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllTagPosts(Long tagId) {
        if (tagId == null) {
            throw new ValidationException("标签ID不能为空");
        }

        try {
            int result = postTagMapper.deleteByTagId(tagId);
            
            // 更新标签使用次数（应该为0）
            updateTagUseCount(tagId);

            log.info("标签所有文章关联删除成功，标签ID: {}, 删除数量: {}", tagId, result);
            return true;
        } catch (Exception e) {
            log.error("标签所有文章关联删除失败，标签ID: {}", tagId, e);
            throw new BusinessException("标签所有文章关联删除失败");
        }
    }

    @Override
    public List<Long> getTagIdsByPostId(Long postId) {
        if (postId == null) {
            return List.of();
        }
        return postTagMapper.selectTagIdsByPostId(postId);
    }

    @Override
    public List<Long> getPostIdsByTagId(Long tagId) {
        if (tagId == null) {
            return List.of();
        }
        return postTagMapper.selectPostIdsByTagId(tagId);
    }

    @Override
    public Long countPostsByTagId(Long tagId) {
        if (tagId == null) {
            return 0L;
        }
        return postTagMapper.countByTagId(tagId);
    }

    @Override
    public Long countTagsByPostId(Long postId) {
        if (postId == null) {
            return 0L;
        }

        QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        return postTagMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean hasPostTag(Long postId, Long tagId) {
        if (postId == null || tagId == null) {
            return false;
        }

        QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("tag_id", tagId);
        return postTagMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<PostTag> getAllPostTags() {
        return postTagMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemovePostTags(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return true;
        }

        try {
            // 获取要删除的标签ID列表，用于更新使用次数
            List<Long> allTagIds = new java.util.ArrayList<>();
            for (Long postId : postIds) {
                allTagIds.addAll(getTagIdsByPostId(postId));
            }

            QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("post_id", postIds);
            int result = postTagMapper.delete(queryWrapper);

            // 更新相关标签的使用次数
            for (Long tagId : allTagIds.stream().distinct().collect(java.util.stream.Collectors.toList())) {
                updateTagUseCount(tagId);
            }

            log.info("批量删除文章标签关联成功，文章数量: {}, 删除关联数量: {}", postIds.size(), result);
            return true;
        } catch (Exception e) {
            log.error("批量删除文章标签关联失败", e);
            throw new BusinessException("批量删除文章标签关联失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemoveTagPosts(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true;
        }

        try {
            QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("tag_id", tagIds);
            int result = postTagMapper.delete(queryWrapper);

            // 更新相关标签的使用次数
            for (Long tagId : tagIds) {
                updateTagUseCount(tagId);
            }

            log.info("批量删除标签文章关联成功，标签数量: {}, 删除关联数量: {}", tagIds.size(), result);
            return true;
        } catch (Exception e) {
            log.error("批量删除标签文章关联失败", e);
            throw new BusinessException("批量删除标签文章关联失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanInvalidRelations() {
        try {
            // 这里需要复杂的SQL查询来找出无效关联，简化实现
            // 实际场景中应该查询post_tag表中post_id或tag_id在对应表中不存在的记录
            
            List<PostTag> allRelations = postTagMapper.selectList(null);
            int cleanCount = 0;

            for (PostTag relation : allRelations) {
                // 检查文章是否存在
                if (postMapper.selectById(relation.getPostId()) == null) {
                    postTagMapper.deleteById(relation.getId());
                    cleanCount++;
                    continue;
                }

                // 检查标签是否存在
                if (tagMapper.selectById(relation.getTagId()) == null) {
                    postTagMapper.deleteById(relation.getId());
                    cleanCount++;
                }
            }

            log.info("清理无效文章标签关联完成，清理数量: {}", cleanCount);
            return cleanCount;
        } catch (Exception e) {
            log.error("清理无效文章标签关联失败", e);
            throw new BusinessException("清理无效文章标签关联失败");
        }
    }

    @Override
    public boolean updateTagUseCount(Long tagId) {
        if (tagId == null) {
            return false;
        }

        try {
            Long useCount = countPostsByTagId(tagId);
            tagMapper.updateUseCount(tagId, useCount);
            return true;
        } catch (Exception e) {
            log.error("更新标签使用次数失败，标签ID: {}", tagId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncAllTagUseCounts() {
        try {
            List<org.easytech.blogs.entity.Tag> allTags = tagMapper.selectAllTags();
            int successCount = 0;

            for (org.easytech.blogs.entity.Tag tag : allTags) {
                if (updateTagUseCount(tag.getId())) {
                    successCount++;
                }
            }

            log.info("同步所有标签使用次数完成，成功更新: {}/{}", successCount, allTags.size());
            return successCount == allTags.size();
        } catch (Exception e) {
            log.error("同步所有标签使用次数失败", e);
            throw new BusinessException("同步所有标签使用次数失败");
        }
    }
}