package org.easytech.blogs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.common.PageResult;
import org.easytech.blogs.common.Result;
import org.easytech.blogs.dto.PostCreateRequest;
import org.easytech.blogs.dto.PostResponse;
import org.easytech.blogs.dto.PostUpdateRequest;
import org.easytech.blogs.entity.Post;
import org.easytech.blogs.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/posts")
@CrossOrigin
@Validated
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 发布文章
     * POST /api/posts
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<PostResponse> publishPost(@Validated @RequestBody PostCreateRequest request) {
        try {
            Post post = postService.publishPost(
                request.getTitle(),
                request.getSummary(),
                request.getContent(),
                request.getAuthorId(),
                request.getCategoryId(),
                request.getTagNames()
            );
            PostResponse response = convertToResponse(post);
            return Result.success("文章发布成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 保存草稿
     * POST /api/posts/drafts
     */
    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    public Result<PostResponse> saveDraft(@Validated @RequestBody PostCreateRequest request) {
        try {
            Post post = postService.saveDraft(
                request.getTitle(),
                request.getContent(),
                request.getSummary(),
                request.getAuthorId(),
                request.getCategoryId(),
                request.getTagNames()
            );
            PostResponse response = convertToResponse(post);
            return Result.success("草稿保存成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取文章详情
     * GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    public Result<PostResponse> getPostById(@PathVariable Long id) {
        Post post = postService.getPostDetail(id);
        if (post == null) {
            return Result.notFound();
        }
        
        PostResponse response = convertToResponse(post);
        return Result.success(response);
    }

    /**
     * 更新文章
     * PUT /api/posts/{id}
     */
    @PutMapping("/{id}")
    public Result<PostResponse> updatePost(@PathVariable Long id, 
                                         @Validated @RequestBody PostUpdateRequest request) {
        try {
            Post post = postService.updatePost(
                id,
                request.getTitle(),
                request.getSummary(),
                request.getContent(),
                request.getCategoryId(),
                request.getTagNames()
            );
            PostResponse response = convertToResponse(post);
            return Result.success("文章更新成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文章
     * DELETE /api/posts/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id,
                                 @RequestHeader(value = "User-Id", required = false) Long userId) {
        boolean success = postService.deletePost(id, userId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 获取文章列表（分页）
     * GET /api/posts?page=1&size=10
     */
    @GetMapping
    public Result<PageResult<PostResponse>> getPosts(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword) {
        
        Page<Post> postPage = new Page<>(page, size);
        Page<Post> result;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            result = (Page<Post>) postService.searchPosts(postPage, keyword.trim());
        } else if (categoryId != null) {
            result = (Page<Post>) postService.getPostsByCategory(postPage, categoryId);
        } else if (authorId != null) {
            result = (Page<Post>) postService.getPostsByAuthor(postPage, authorId);
        } else if (tagId != null) {
            result = (Page<Post>) postService.getPostsByTag(postPage, tagId);
        } else {
            result = (Page<Post>) postService.getPublishedPostPage(postPage);
        }
        
        PageResult<PostResponse> pageResult = new PageResult<>();
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
     * 获取热门文章
     * GET /api/posts/hot?limit=10
     */
    @GetMapping("/hot")
    public Result<List<PostResponse>> getHotPosts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Post> posts = postService.getHotPosts(limit);
        List<PostResponse> responses = posts.stream()
                .map(this::convertToResponse)
                .toList();
        return Result.success(responses);
    }

    /**
     * 获取最新文章
     * GET /api/posts/latest?limit=10
     */
    @GetMapping("/latest")
    public Result<List<PostResponse>> getLatestPosts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Post> posts = postService.getLatestPosts(limit);
        List<PostResponse> responses = posts.stream()
                .map(this::convertToResponse)
                .toList();
        return Result.success(responses);
    }

    /**
     * 获取推荐文章
     * GET /api/posts/recommend?limit=10
     */
    @GetMapping("/recommend")
    public Result<List<PostResponse>> getRecommendPosts(@RequestParam(defaultValue = "10") Integer limit) {
        List<Post> posts = postService.getRecommendPosts(limit);
        List<PostResponse> responses = posts.stream()
                .map(this::convertToResponse)
                .toList();
        return Result.success(responses);
    }

    /**
     * 点赞文章
     * POST /api/posts/{id}/likes
     */
    @PostMapping("/{id}/likes")
    public Result<Void> likePost(@PathVariable Long id,
                                @RequestHeader(value = "User-Id") Long userId) {
        boolean success = postService.likePost(id, userId);
        if (success) {
            return Result.success("点赞成功");
        } else {
            return Result.error("点赞失败");
        }
    }

    /**
     * 取消点赞文章
     * DELETE /api/posts/{id}/likes
     */
    @DeleteMapping("/{id}/likes")
    public Result<Void> unlikePost(@PathVariable Long id,
                                 @RequestHeader(value = "User-Id") Long userId) {
        boolean success = postService.unlikePost(id, userId);
        if (success) {
            return Result.success("取消点赞成功");
        } else {
            return Result.error("取消点赞失败");
        }
    }

    /**
     * 设置文章置顶（管理员功能）
     * PATCH /api/posts/{id}/top
     */
    @PatchMapping("/{id}/top")
    public Result<Void> setPostTop(@PathVariable Long id,
                                  @RequestParam boolean isTop) {
        boolean success = postService.topPost(id, isTop);
        if (success) {
            return Result.success("操作成功");
        } else {
            return Result.error("操作失败");
        }
    }

    /**
     * 更新文章状态（管理员功能）
     * PATCH /api/posts/{id}/status
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updatePostStatus(@PathVariable Long id,
                                        @RequestParam Integer status) {
        boolean success = postService.updatePostStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    /**
     * 检查用户是否已点赞文章
     * GET /api/posts/{id}/likes/check?userId=xxx
     */
    @GetMapping("/{id}/likes/check")
    public Result<Boolean> checkLikeStatus(@PathVariable Long id,
                                         @RequestParam Long userId) {
        boolean hasLiked = postService.hasLiked(id, userId);
        return Result.success(hasLiked);
    }

    /**
     * 获取文章统计信息
     * GET /api/posts/{id}/statistics
     */
    @GetMapping("/{id}/statistics")
    public Result<PostResponse> getPostStatistics(@PathVariable Long id) {
        Post post = postService.getPostStatistics(id);
        if (post == null) {
            return Result.notFound();
        }
        
        PostResponse response = convertToResponse(post);
        return Result.success(response);
    }

    /**
     * 转换Post实体为PostResponse DTO
     */
    private PostResponse convertToResponse(Post post) {
        PostResponse response = new PostResponse();
        BeanUtils.copyProperties(post, response);
        return response;
    }
}