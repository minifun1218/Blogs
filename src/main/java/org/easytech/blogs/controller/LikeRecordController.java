package org.easytech.blogs.controller;

import org.easytech.blogs.common.Result;
import org.easytech.blogs.entity.LikeRecord;
import org.easytech.blogs.service.LikeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点赞记录控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/likes")
@CrossOrigin
@Validated
public class LikeRecordController {

    private final LikeRecordService likeRecordService;

    @Autowired
    public LikeRecordController(LikeRecordService likeRecordService) {
        this.likeRecordService = likeRecordService;
    }

    /**
     * 用户点赞或取消点赞
     * POST /api/likes
     */
    @PostMapping
    public Result<String> toggleLike(
            @RequestParam Long userId,
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        
        boolean result = likeRecordService.toggleLike(userId, targetType, targetId);
        if (result) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    /**
     * 检查用户是否已点赞
     * GET /api/likes/check
     */
    @GetMapping("/check")
    public Result<Boolean> checkUserLike(
            @RequestParam Long userId,
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        
        boolean isLiked = likeRecordService.hasLiked(userId, targetType, targetId);
        return Result.success(isLiked);
    }

    /**
     * 获取目标的点赞数量
     * GET /api/likes/count
     */
    @GetMapping("/count")
    public Result<Long> getLikeCount(
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        
        Long count = likeRecordService.countLikes(targetType, targetId);
        return Result.success(count);
    }

    /**
     * 获取用户的点赞记录
     * GET /api/likes/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result<List<LikeRecord>> getUserLikeRecords(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer targetType) {
        
        List<LikeRecord> records = likeRecordService.getUserLikeRecords(userId, targetType);
        return Result.success(records);
    }

    /**
     * 获取目标的点赞记录
     * GET /api/likes/target
     */
    @GetMapping("/target")
    public Result<List<LikeRecord>> getTargetLikeRecords(
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        
        List<LikeRecord> records = likeRecordService.getTargetLikeRecords(targetType, targetId);
        return Result.success(records);
    }

    /**
     * 获取最近的点赞记录
     * GET /api/likes/recent
     */
    @GetMapping("/recent")
    public Result<List<LikeRecord>> getRecentLikeRecords(
            @RequestParam Integer targetType,
            @RequestParam Long targetId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        List<LikeRecord> records = likeRecordService.getRecentLikeRecords(targetType, targetId, limit);
        return Result.success(records);
    }

    /**
     * 用户点赞操作
     * POST /api/likes/like
     */
    @PostMapping("/like")
    public Result<String> like(
            @RequestParam Long userId,
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        
        boolean success = likeRecordService.like(userId, targetType, targetId);
        if (success) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }

    /**
     * 用户取消点赞操作
     * POST /api/likes/unlike
     */
    @PostMapping("/unlike")
    public Result<String> unlike(
            @RequestParam Long userId,
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        
        boolean success = likeRecordService.unlike(userId, targetType, targetId);
        if (success) {
            return Result.success("取消点赞成功");
        }
        return Result.error("取消点赞失败");
    }

    /**
     * 获取用户点赞数量统计
     * GET /api/likes/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public Result<Long> getUserLikeCount(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer targetType) {
        
        Long count = likeRecordService.countUserLikes(userId, targetType);
        return Result.success(count);
    }

    /**
     * 获取用户点赞的目标列表
     * GET /api/likes/user/{userId}/targets
     */
    @GetMapping("/user/{userId}/targets")
    public Result<List<Long>> getUserLikedTargets(
            @PathVariable Long userId,
            @RequestParam Integer targetType) {
        
        List<Long> targets = likeRecordService.getUserLikedTargets(userId, targetType);
        return Result.success(targets);
    }

    /**
     * 根据ID获取点赞记录
     * GET /api/likes/{id}
     */
    @GetMapping("/{id}")
    public Result<LikeRecord> getLikeRecordById(@PathVariable Long id) {
        LikeRecord record = likeRecordService.getLikeRecordById(id);
        if (record == null) {
            return Result.notFound();
        }
        return Result.success(record);
    }

    /**
     * 批量删除用户的点赞记录（管理员功能）
     * DELETE /api/likes/user/{userId}
     */
    @DeleteMapping("/user/{userId}")
    public Result<String> deleteUserLikeRecords(@PathVariable Long userId) {
        boolean success = likeRecordService.deleteUserLikeRecords(userId);
        if (success) {
            return Result.success("用户点赞记录删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除目标的点赞记录（管理员功能）
     * DELETE /api/likes/target
     */
    @DeleteMapping("/target")
    public Result<String> deleteTargetLikeRecords(
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        
        boolean success = likeRecordService.deleteTargetLikeRecords(targetType, targetId);
        if (success) {
            return Result.success("目标点赞记录删除成功");
        }
        return Result.error("删除失败");
    }
}