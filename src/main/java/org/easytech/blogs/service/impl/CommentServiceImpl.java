package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.Comment;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ResourceNotFoundException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.CommentMapper;
import org.easytech.blogs.mapper.PostMapper;
import org.easytech.blogs.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishComment(Comment comment) {
        if (comment == null || !StringUtils.hasText(comment.getContent())) {
            throw new ValidationException("评论内容不能为空");
        }

        if (comment.getPostId() == null || comment.getUserId() == null) {
            throw new ValidationException("文章ID和用户ID不能为空");
        }

        try {
            if (postMapper.selectById(comment.getPostId()) == null) {
                throw new ResourceNotFoundException("文章不存在");
            }

            if (comment.getStatus() == null) {
                comment.setStatus(0);
            }

            int result = commentMapper.insert(comment);
            return result > 0;
        } catch (Exception e) {
            log.error("发表评论失败: {}", e.getMessage(), e);
            throw new BusinessException("发表评论失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment addComment(Long postId, Long userId, String content, Long parentId) {
        if (postId == null || userId == null || !StringUtils.hasText(content)) {
            throw new ValidationException("文章ID、用户ID和评论内容不能为空");
        }

        try {
            if (postMapper.selectById(postId) == null) {
                throw new ResourceNotFoundException("文章不存在");
            }

            if (parentId != null && commentMapper.selectById(parentId) == null) {
                throw new ResourceNotFoundException("父评论不存在");
            }

            Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setUserId(userId);
            comment.setContent(content);
            comment.setParentId(parentId);
            comment.setStatus(1);
            comment.setLikeCount(0L);

            int result = commentMapper.insert(comment);
            if (result > 0) {
                return comment;
            } else {
                throw new BusinessException("评论保存失败");
            }
        } catch (Exception e) {
            log.error("添加评论失败: {}", e.getMessage(), e);
            throw new BusinessException("添加评论失败: " + e.getMessage());
        }
    }

    @Override
    public boolean replyComment(Comment comment) {
        return publishComment(comment);
    }

    @Override
    public boolean deleteComment(Long commentId, Long userId) {
        if (commentId == null) {
            throw new ValidationException("评论ID不能为空");
        }

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new ResourceNotFoundException("评论不存在");
        }

        try {
            int result = commentMapper.deleteById(commentId);
            return result > 0;
        } catch (Exception e) {
            log.error("评论删除失败，评论ID: {}", commentId, e);
            throw new BusinessException("评论删除失败");
        }
    }

    @Override
    public boolean auditComment(Long commentId, Integer status) {
        if (commentId == null || status == null) {
            throw new ValidationException("评论ID和状态不能为空");
        }

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new ResourceNotFoundException("评论不存在");
        }

        comment.setStatus(status);
        return commentMapper.updateById(comment) > 0;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        if (postId == null) {
            throw new ValidationException("文章ID不能为空");
        }
        // 这里应该实现具体的查询逻辑，暂时返回空列表
        return List.of();
    }

    @Override
    public IPage<Comment> getCommentPage(Page<Comment> page) {
        return commentMapper.selectPage(page, null);
    }

    @Override
    public IPage<Comment> getCommentsByUserId(Page<Comment> page, Long userId) {
        // 这里应该实现具体的查询逻辑
        return commentMapper.selectPage(page, null);
    }

    @Override
    public IPage<Comment> getCommentsByStatus(Page<Comment> page, Integer status) {
        // 这里应该实现具体的查询逻辑
        return commentMapper.selectPage(page, null);
    }

    @Override
    public boolean likeComment(Long commentId, Long userId, boolean isLike) {
        if (commentId == null || userId == null) {
            throw new ValidationException("评论ID和用户ID不能为空");
        }

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new ResourceNotFoundException("评论不存在");
        }

        // 这里应该实现点赞逻辑，暂时返回true
        return true;
    }

    @Override
    public boolean hasLiked(Long commentId, Long userId) {
        // 这里应该实现检查点赞状态的逻辑
        return false;
    }

    @Override
    public Long countCommentsByPostId(Long postId) {
        if (postId == null) {
            throw new ValidationException("文章ID不能为空");
        }
        // 这里应该实现统计逻辑
        return 0L;
    }

    @Override
    public Long countCommentsByUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }
        // 这里应该实现统计逻辑
        return 0L;
    }

    @Override
    public boolean batchAuditComments(List<Long> commentIds, Integer status) {
        if (commentIds == null || commentIds.isEmpty() || status == null) {
            throw new ValidationException("评论ID列表和状态不能为空");
        }

        try {
            for (Long commentId : commentIds) {
                auditComment(commentId, status);
            }
            return true;
        } catch (Exception e) {
            log.error("批量审核评论失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Comment> getChildComments(Long parentId) {
        if (parentId == null) {
            throw new ValidationException("父评论ID不能为空");
        }
        // 这里应该实现查询子评论的逻辑
        return List.of();
    }

    @Override
    public Comment getCommentById(Long commentId) {
        if (commentId == null) {
            throw new ValidationException("评论ID不能为空");
        }
        return commentMapper.selectById(commentId);
    }

    @Override
    public List<Comment> buildCommentTree(List<Comment> comments) {
        // 这里应该实现构建评论树的逻辑
        return comments;
    }
}