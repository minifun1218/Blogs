package org.easytech.blogs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 * 提供评论相关的业务逻辑处理
 */
public interface CommentService {

    /**
     * 发表评论
     * @param comment 评论信息
     * @return 发表结果
     */
    boolean publishComment(Comment comment);

    /**
     * 添加评论（新方法）
     * @param postId 文章ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param parentId 父评论ID
     * @return 评论信息
     */
    Comment addComment(Long postId, Long userId, String content, Long parentId);

    /**
     * 回复评论
     * @param comment 回复评论信息
     * @return 回复结果
     */
    boolean replyComment(Comment comment);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID（权限验证）
     * @return 删除结果
     */
    boolean deleteComment(Long commentId, Long userId);

    /**
     * 审核评论
     * @param commentId 评论ID
     * @param status 审核状态
     * @return 审核结果
     */
    boolean auditComment(Long commentId, Integer status);

    /**
     * 根据文章ID获取评论列表（树形结构）
     * @param postId 文章ID
     * @return 评论树形列表
     */
    List<Comment> getCommentsByPostId(Long postId);

    /**
     * 分页查询评论
     * @param page 分页参数
     * @return 评论分页列表
     */
    IPage<Comment> getCommentPage(Page<Comment> page);

    /**
     * 根据用户ID分页查询评论
     * @param page 分页参数
     * @param userId 用户ID
     * @return 评论分页列表
     */
    IPage<Comment> getCommentsByUserId(Page<Comment> page, Long userId);

    /**
     * 根据状态分页查询评论
     * @param page 分页参数
     * @param status 评论状态
     * @return 评论分页列表
     */
    IPage<Comment> getCommentsByStatus(Page<Comment> page, Integer status);

    /**
     * 点赞/取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @param isLike 是否点赞
     * @return 操作结果
     */
    boolean likeComment(Long commentId, Long userId, boolean isLike);

    /**
     * 取消点赞评论（别名方法）
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 操作结果
     */
    default boolean unlikeComment(Long commentId, Long userId) {
        return likeComment(commentId, userId, false);
    }

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean hasLiked(Long commentId, Long userId);

    /**
     * 统计文章评论数
     * @param postId 文章ID
     * @return 评论数量
     */
    Long countCommentsByPostId(Long postId);

    /**
     * 统计文章评论数（别名方法）
     * @param postId 文章ID
     * @return 评论数量
     */
    default Long countByPostId(Long postId) {
        return countCommentsByPostId(postId);
    }

    /**
     * 统计用户评论数
     * @param userId 用户ID
     * @return 评论数量
     */
    Long countCommentsByUserId(Long userId);

    /**
     * 统计用户评论数（别名方法）
     * @param userId 用户ID
     * @return 评论数量
     */
    default Long countByUserId(Long userId) {
        return countCommentsByUserId(userId);
    }

    /**
     * 批量审核评论
     * @param commentIds 评论ID列表
     * @param status 审核状态
     * @return 审核结果
     */
    boolean batchAuditComments(List<Long> commentIds, Integer status);

    /**
     * 获取子评论列表
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    List<Comment> getChildComments(Long parentId);

    /**
     * 根据ID获取评论
     * @param commentId 评论ID
     * @return 评论信息
     */
    Comment getCommentById(Long commentId);

    /**
     * 构建评论树形结构
     * @param comments 评论列表
     * @return 树形结构评论列表
     */
    List<Comment> buildCommentTree(List<Comment> comments);
}