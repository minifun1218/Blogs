package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.*;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ForbiddenException;
import org.easytech.blogs.exception.ResourceNotFoundException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.*;
import org.easytech.blogs.service.PostService;
import org.easytech.blogs.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章服务实现类
 * 实现文章相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;
    private final LikeRecordMapper likeRecordMapper;
    private final CommentMapper commentMapper;
    private final CategoryMapper categoryMapper;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishPost(Post post, List<String> tagNames) {
        // 参数校验
        if (post == null || !StringUtils.hasText(post.getTitle()) || !StringUtils.hasText(post.getContent())) {
            throw new ValidationException("标题和内容不能为空");
        }

        if (post.getAuthorId() == null) {
            throw new ValidationException("作者信息不能为空");
        }

        try {
            // 设置默认状态
            if (post.getStatus() == null) {
                post.setStatus(1); // 默认发布状态
            }

            // 保存文章
            int result = postMapper.insert(post);
            if (result > 0) {
                // 处理标签关联
                handlePostTags(post.getId(), tagNames);
                
                // 更新分类文章数量
                if (post.getCategoryId() != null) {
                    updateCategoryPostCount(post.getCategoryId());
                }

                log.info("文章发布成功，文章ID: {}, 标题: {}", post.getId(), post.getTitle());
                return true;
            }
        } catch (Exception e) {
            log.error("文章发布失败，标题: {}", post.getTitle(), e);
            throw new BusinessException("文章发布失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post publishPost(String title, String summary, String content, Long authorId, Long categoryId, List<String> tagNames) {
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content) || authorId == null) {
            throw new ValidationException("标题、内容和作者信息不能为空");
        }

        try {
            Post post = new Post();
            post.setTitle(title);
            post.setSummary(summary);
            post.setContent(content);
            post.setAuthorId(authorId);
            post.setCategoryId(categoryId);
            post.setStatus(1); // 发布状态

            int result = postMapper.insert(post);
            if (result > 0) {
                // 处理标签关联
                handlePostTags(post.getId(), tagNames);
                
                // 更新分类文章数量
                if (categoryId != null) {
                    updateCategoryPostCount(categoryId);
                }

                log.info("文章发布成功，文章ID: {}, 标题: {}", post.getId(), title);
                return post;
            }
        } catch (Exception e) {
            log.error("文章发布失败，标题: {}", title, e);
            throw new BusinessException("文章发布失败，请稍后重试");
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post saveDraft(String title, String content, String summary, Long authorId, Long categoryId, List<String> tagNames) {
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content) || authorId == null) {
            throw new ValidationException("标题、内容和作者信息不能为空");
        }

        try {
            Post post = new Post();
            post.setTitle(title);
            post.setSummary(summary);
            post.setContent(content);
            post.setAuthorId(authorId);
            post.setCategoryId(categoryId);
            post.setStatus(0); // 草稿状态

            int result = postMapper.insert(post);
            if (result > 0) {
                // 处理标签关联
                handlePostTags(post.getId(), tagNames);

                log.info("草稿保存成功，文章ID: {}, 标题: {}", post.getId(), title);
                return post;
            }
        } catch (Exception e) {
            log.error("草稿保存失败，标题: {}", title, e);
            throw new BusinessException("草稿保存失败，请稍后重试");
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePost(Post post, List<String> tagNames) {
        if (post == null || post.getId() == null) {
            throw new ValidationException("文章ID不能为空");
        }

        // 检查文章是否存在
        Post existPost = postMapper.selectById(post.getId());
        if (existPost == null) {
            throw new ResourceNotFoundException("文章不存在");
        }

        try {
            int result = postMapper.updateById(post);
            if (result > 0) {
                // 处理标签关联
                handlePostTags(post.getId(), tagNames);
                
                // 如果分类发生变化，更新相关分类的文章数量
                if (post.getCategoryId() != null && !post.getCategoryId().equals(existPost.getCategoryId())) {
                    if (existPost.getCategoryId() != null) {
                        updateCategoryPostCount(existPost.getCategoryId());
                    }
                    updateCategoryPostCount(post.getCategoryId());
                }

                log.info("文章更新成功，文章ID: {}", post.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("文章更新失败，文章ID: {}", post.getId(), e);
            throw new BusinessException("文章更新失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post updatePost(Long postId, String title, String summary, String content, Long categoryId, List<String> tagNames) {
        if (postId == null) {
            throw new ValidationException("文章ID不能为空");
        }

        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            throw new ValidationException("标题和内容不能为空");
        }

        // 检查文章是否存在
        Post existPost = postMapper.selectById(postId);
        if (existPost == null) {
            throw new ResourceNotFoundException("文章不存在");
        }

        try {
            // 更新文章信息
            Post updatePost = new Post();
            updatePost.setId(postId);
            updatePost.setTitle(title);
            updatePost.setSummary(summary);
            updatePost.setContent(content);
            updatePost.setCategoryId(categoryId);

            int result = postMapper.updateById(updatePost);
            if (result > 0) {
                // 处理标签关联
                handlePostTags(postId, tagNames);
                
                // 如果分类发生变化，更新相关分类的文章数量
                if (categoryId != null && !categoryId.equals(existPost.getCategoryId())) {
                    if (existPost.getCategoryId() != null) {
                        updateCategoryPostCount(existPost.getCategoryId());
                    }
                    updateCategoryPostCount(categoryId);
                }

                log.info("文章更新成功，文章ID: {}, 标题: {}", postId, title);
                return getPostById(postId, false);
            }
        } catch (Exception e) {
            log.error("文章更新失败，文章ID: {}, 标题: {}", postId, title, e);
            throw new BusinessException("文章更新失败，请稍后重试");
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePost(Long postId, Long authorId) {
        if (postId == null) {
            throw new ValidationException("文章ID不能为空");
        }

        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("文章不存在");
        }

        // 权限验证：只有作者或管理员可以删除文章
        if (authorId != null && !post.getAuthorId().equals(authorId)) {
            throw new ForbiddenException("没有权限删除此文章");
        }

        try {
            // 删除文章标签关联
            postTagMapper.deleteByPostId(postId);
            
            // 逻辑删除文章
            int result = postMapper.deleteById(postId);
            if (result > 0) {
                // 更新分类文章数量
                if (post.getCategoryId() != null) {
                    updateCategoryPostCount(post.getCategoryId());
                }

                log.info("文章删除成功，文章ID: {}", postId);
                return true;
            }
        } catch (Exception e) {
            log.error("文章删除失败，文章ID: {}", postId, e);
            throw new BusinessException("文章删除失败，请稍后重试");
        }

        return false;
    }

    @Override
    public Post getPostById(Long postId, boolean incrementView) {
        if (postId == null) {
            return null;
        }

        Post post = postMapper.selectById(postId);
        if (post != null && incrementView) {
            // 异步增加浏览量
            try {
                incrementViewCount(postId);
            } catch (Exception e) {
                log.warn("增加文章浏览量失败，文章ID: {}", postId, e);
            }
        }

        return post;
    }

    @Override
    public IPage<Post> getPublishedPostPage(Page<Post> page) {
        return postMapper.selectPublishedPostPage(page);
    }

    @Override
    public IPage<Post> getPostsByCategory(Page<Post> page, Long categoryId) {
        if (categoryId == null) {
            return getPublishedPostPage(page);
        }
        return postMapper.selectPostsByCategory(page, categoryId);
    }

    @Override
    public IPage<Post> getPostsByAuthor(Page<Post> page, Long authorId) {
        if (authorId == null) {
            return page;
        }
        return postMapper.selectPostsByAuthor(page, authorId);
    }

    @Override
    public IPage<Post> getPostsByTag(Page<Post> page, Long tagId) {
        if (tagId == null) {
            return getPublishedPostPage(page);
        }
        return postMapper.selectPostsByTag(page, tagId);
    }

    @Override
    public IPage<Post> searchPosts(Page<Post> page, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getPublishedPostPage(page);
        }
        return postMapper.searchPosts(page, keyword.trim());
    }

    @Override
    public List<Post> getHotPosts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return postMapper.selectHotPosts(limit);
    }

    @Override
    public boolean incrementViewCount(Long postId) {
        if (postId == null) {
            return false;
        }

        try {
            return postMapper.incrementViewCount(postId) > 0;
        } catch (Exception e) {
            log.error("增加文章浏览量失败，文章ID: {}", postId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likePost(Long postId, Long userId, boolean isLike) {
        if (postId == null || userId == null) {
            throw new ValidationException("文章ID和用户ID不能为空");
        }

        try {
            LikeRecord existRecord = likeRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<LikeRecord>()
                    .eq("user_id", userId)
                    .eq("target_type", 1) // 1-文章
                    .eq("target_id", postId)
            );

            if (isLike) {
                // 点赞
                if (existRecord == null) {
                    LikeRecord likeRecord = new LikeRecord();
                    likeRecord.setUserId(userId);
                    likeRecord.setTargetType(1);
                    likeRecord.setTargetId(postId);
                    likeRecord.setStatus(1);
                    likeRecordMapper.insert(likeRecord);
                } else if (existRecord.getStatus() != 1) {
                    existRecord.setStatus(1);
                    likeRecordMapper.updateById(existRecord);
                }
            } else {
                // 取消点赞
                if (existRecord != null && existRecord.getStatus() == 1) {
                    existRecord.setStatus(0);
                    likeRecordMapper.updateById(existRecord);
                }
            }

            // 更新文章点赞数
            updatePostLikeCount(postId);
            return true;
        } catch (Exception e) {
            log.error("文章点赞操作失败，文章ID: {}, 用户ID: {}", postId, userId, e);
            throw new BusinessException("点赞操作失败");
        }
    }

    @Override
    public boolean hasLiked(Long postId, Long userId) {
        if (postId == null || userId == null) {
            return false;
        }
        return likeRecordMapper.hasLiked(userId, 1, postId);
    }

    @Override
    public boolean updatePostStatus(Long postId, Integer status) {
        if (postId == null || status == null) {
            throw new ValidationException("参数不能为空");
        }

        Post post = new Post();
        post.setId(postId);
        post.setStatus(status);

        try {
            int result = postMapper.updateById(post);
            if (result > 0) {
                log.info("文章状态更新成功，文章ID: {}, 状态: {}", postId, status);
                return true;
            }
        } catch (Exception e) {
            log.error("文章状态更新失败，文章ID: {}", postId, e);
            throw new BusinessException("状态更新失败");
        }

        return false;
    }

    @Override
    public boolean setPostTop(Long postId, boolean isTop) {
        if (postId == null) {
            throw new ValidationException("文章ID不能为空");
        }

        Post post = new Post();
        post.setId(postId);
        post.setIsTop(isTop ? 1 : 0);

        try {
            int result = postMapper.updateById(post);
            if (result > 0) {
                log.info("文章置顶设置成功，文章ID: {}, 置顶: {}", postId, isTop);
                return true;
            }
        } catch (Exception e) {
            log.error("文章置顶设置失败，文章ID: {}", postId, e);
            throw new BusinessException("置顶设置失败");
        }

        return false;
    }

    @Override
    public List<Tag> getPostTags(Long postId) {
        if (postId == null) {
            return List.of();
        }
        return tagMapper.selectTagsByPostId(postId);
    }

    @Override
    public Long countPosts(Integer status) {
        if (status == null) {
            return postMapper.selectCount(null);
        }
        return postMapper.countByStatus(status);
    }

    @Override
    public List<Post> getRecommendPosts(Long postId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }

        // 简单的推荐策略：获取同分类的其他热门文章
        if (postId != null) {
            Post currentPost = postMapper.selectById(postId);
            if (currentPost != null && currentPost.getCategoryId() != null) {
                Page<Post> page = new Page<>(1, limit);
                IPage<Post> categoryPosts = postMapper.selectPostsByCategory(page, currentPost.getCategoryId());
                return categoryPosts.getRecords().stream()
                    .filter(post -> !post.getId().equals(postId))
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());
            }
        }

        // 如果没有当前文章信息，返回热门文章
        return getHotPosts(limit);
    }

    @Override
    public Post getPostStatistics(Long postId) {
        return getPostById(postId, false);
    }

    /**
     * 处理文章标签关联
     */
    private void handlePostTags(Long postId, List<String> tagNames) {
        // 删除原有标签关联
        postTagMapper.deleteByPostId(postId);

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                if (StringUtils.hasText(tagName)) {
                    // 查找或创建标签
                    Tag tag = tagMapper.findByName(tagName.trim());
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName.trim());
                        tag.setUseCount(0L);
                        tagMapper.insert(tag);
                    }

                    // 创建文章标签关联
                    PostTag postTag = new PostTag();
                    postTag.setPostId(postId);
                    postTag.setTagId(tag.getId());
                    postTagMapper.insert(postTag);

                    // 更新标签使用次数
                    Long useCount = postTagMapper.countByTagId(tag.getId());
                    tagMapper.updateUseCount(tag.getId(), useCount);
                }
            }
        }
    }

    /**
     * 更新分类文章数量
     */
    private void updateCategoryPostCount(Long categoryId) {
        if (categoryId != null) {
            Long postCount = postMapper.countByStatus(1); // 只统计已发布的文章
            categoryMapper.updatePostCount(categoryId, postCount);
        }
    }

    /**
     * 更新文章点赞数
     */
    private void updatePostLikeCount(Long postId) {
        Long likeCount = likeRecordMapper.countLikes(1, postId);
        postMapper.updateLikeCount(postId, likeCount);
    }

    @Override
    public List<Post> getLatestPosts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return postMapper.selectLatestPosts(limit);
    }

    @Override
    public boolean isPostAuthor(Long postId, String username) {
        if (postId == null || !StringUtils.hasText(username)) {
            return false;
        }
        
        try {
            Post post = postMapper.selectById(postId);
            if (post == null) {
                return false;
            }
            
            // 通过UserService获取用户信息
            User user = userService.findByUsername(username);
            if (user == null) {
                return false;
            }
            
            return post.getAuthorId().equals(user.getId());
        } catch (Exception e) {
            log.error("检查文章作者权限失败，文章ID: {}, 用户名: {}", postId, username, e);
            return false;
        }
    }

    @Override
    public boolean isPostAuthorByUserId(Long postId, Long userId) {
        if (postId == null || userId == null) {
            return false;
        }
        
        try {
            Post post = postMapper.selectById(postId);
            return post != null && post.getAuthorId().equals(userId);
        } catch (Exception e) {
            log.error("检查文章作者权限失败，文章ID: {}, 用户ID: {}", postId, userId, e);
            return false;
        }
    }
}