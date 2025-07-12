package org.easytech.blogs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.common.PageResult;
import org.easytech.blogs.common.Result;
import org.easytech.blogs.dto.CommentCreateRequest;
import org.easytech.blogs.dto.CommentResponse;
import org.easytech.blogs.entity.Comment;
import org.easytech.blogs.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/comments")
@CrossOrigin
@Validated
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 发表评论
     * POST /api/comments
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<CommentResponse> createComment(@Validated @RequestBody CommentCreateRequest request) {
        try {
            Comment comment = commentService.addComment(
                request.getPostId(),
                request.getUserId(),
                request.getContent(),
                request.getParentId()
            );
            CommentResponse response = convertToResponse(comment);
            return Result.success("评论发表成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评论详情
     * GET /api/comments/{id}
     */
    @GetMapping("/{id}")
    public Result<CommentResponse> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return Result.notFound();
        }
        
        CommentResponse response = convertToResponse(comment);
        return Result.success(response);
    }

    /**
     * 获取评论列表（分页）
     * GET /api/comments?page=1&size=10&status=1
     */
    @GetMapping
    public Result<PageResult<CommentResponse>> getComments(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        
        Page<Comment> commentPage = new Page<>(page, size);
        Page<Comment> result;
        
        if (status != null) {
            result = (Page<Comment>) commentService.getCommentsByStatus(commentPage, status);
        } else {
            result = (Page<Comment>) commentService.getCommentPage(commentPage);
        }
        
        PageResult<CommentResponse> pageResult = new PageResult<>();
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        
        pageResult.setRecords(result.getRecords().stream()
                .map(this::convertToResponse)
                .toList());
        
        return Result.success(pageResult);
    }

    /**
     * 根据文章ID获取评论（树形结构）
     * GET /api/comments/posts/{postId}
     */
    @GetMapping("/posts/{postId}")
    public Result<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<CommentResponse> responses = comments.stream()
                .map(this::convertToResponse)
                .toList();
        return Result.success(responses);
    }

    /**
     * 根据用户ID获取评论（分页）
     * GET /api/comments/users/{userId}?page=1&size=10
     */
    @GetMapping("/users/{userId}")
    public Result<PageResult<CommentResponse>> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        
        Page<Comment> commentPage = new Page<>(page, size);
        Page<Comment> result = (Page<Comment>) commentService.getCommentsByUserId(commentPage, userId);
        
        PageResult<CommentResponse> pageResult = new PageResult<>();
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        
        pageResult.setRecords(result.getRecords().stream()
                .map(this::convertToResponse)
                .toList());
        
        return Result.success(pageResult);
    }

    /**
     * 删除评论（逻辑删除）
     * DELETE /api/comments/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                    @RequestHeader(value = "User-Id", required = false) Long userId) {
        boolean success = commentService.deleteComment(id, userId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 点赞评论
     * POST /api/comments/{id}/likes
     */
    @PostMapping("/{id}/likes")
    public Result<Void> likeComment(@PathVariable Long id,
                                  @RequestHeader(value = "User-Id") Long userId) {
        boolean success = commentService.likeComment(id, userId, true);
        if (success) {
            return Result.success("点赞成功");
        } else {
            return Result.error("点赞失败");
        }
    }

    /**
     * 取消点赞评论
     * DELETE /api/comments/{id}/likes
     */
    @DeleteMapping("/{id}/likes")
    public Result<Void> unlikeComment(@PathVariable Long id,
                                    @RequestHeader(value = "User-Id") Long userId) {
        boolean success = commentService.unlikeComment(id, userId);
        if (success) {
            return Result.success("取消点赞成功");
        } else {
            return Result.error("取消点赞失败");
        }
    }

    /**
     * 审核评论（管理员功能）
     * PATCH /api/comments/{id}/status
     */
    @PatchMapping("/{id}/status")
    public Result<Void> auditComment(@PathVariable Long id,
                                   @RequestParam Integer status) {
        boolean success = commentService.auditComment(id, status);
        if (success) {
            return Result.success("审核成功");
        } else {
            return Result.error("审核失败");
        }
    }

    /**
     * 批量审核评论（管理员功能）
     * PATCH /api/comments/batch-audit
     */
    @PatchMapping("/batch-audit")
    public Result<Void> batchAuditComments(@RequestParam List<Long> commentIds,
                                         @RequestParam Integer status) {
        boolean success = commentService.batchAuditComments(commentIds, status);
        if (success) {
            return Result.success("批量审核成功");
        } else {
            return Result.error("批量审核失败");
        }
    }

    /**
     * 统计文章评论数
     * GET /api/comments/posts/{postId}/count
     */
    @GetMapping("/posts/{postId}/count")
    public Result<Long> countCommentsByPostId(@PathVariable Long postId) {
        Long count = commentService.countByPostId(postId);
        return Result.success(count);
    }

    /**
     * 统计用户评论数
     * GET /api/comments/users/{userId}/count
     */
    @GetMapping("/users/{userId}/count")
    public Result<Long> countCommentsByUserId(@PathVariable Long userId) {
        Long count = commentService.countByUserId(userId);
        return Result.success(count);
    }

    /**
     * 检查用户是否已点赞评论
     * GET /api/comments/{id}/likes/check?userId=xxx
     */
    @GetMapping("/{id}/likes/check")
    public Result<Boolean> checkLikeStatus(@PathVariable Long id,
                                         @RequestParam Long userId) {
        boolean hasLiked = commentService.hasLiked(id, userId);
        return Result.success(hasLiked);
    }

    /**
     * 转换Comment实体为CommentResponse DTO
     */
    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        BeanUtils.copyProperties(comment, response);
        return response;
    }
}