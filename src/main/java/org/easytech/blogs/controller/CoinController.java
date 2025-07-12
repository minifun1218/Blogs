package org.easytech.blogs.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.common.PageResult;
import org.easytech.blogs.common.Result;
import org.easytech.blogs.entity.Coin;
import org.easytech.blogs.entity.UserCoin;
import org.easytech.blogs.service.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分记录控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/coins")
@CrossOrigin
@Validated
public class CoinController {

    private final CoinService coinService;

    @Autowired
    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    /**
     * 分页查询积分记录
     * GET /api/coins?page=1&size=10&userId=1&operationType=1
     */
    @GetMapping
    public Result<PageResult<Coin>> getCoinRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer operationType) {
        
        Page<Coin> pageInfo = new Page<>(page, size);
        IPage<Coin> result;
        
        if (userId != null) {
            result = coinService.getUserCoinRecords(pageInfo, userId);
        } else if (operationType != null) {
            result = coinService.getCoinRecordsByType(pageInfo, operationType);
        } else {
            result = coinService.getUserCoinRecords(pageInfo, null);
        }
        
        return Result.success(PageResult.of(result));
    }

    /**
     * 查询用户积分记录
     * GET /api/coins/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result<PageResult<Coin>> getUserCoinRecords(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Coin> pageInfo = new Page<>(page, size);
        IPage<Coin> result = coinService.getUserCoinRecords(pageInfo, userId);
        
        return Result.success(PageResult.of(result));
    }

    /**
     * 手动添加积分记录（管理员功能）
     * POST /api/coins
     */
    @PostMapping
    public Result<String> addCoinRecord(
            @RequestParam Long userId,
            @RequestParam Integer amount,
            @RequestParam Integer operationType,
            @RequestParam String description,
            @RequestParam(required = false) Long relatedId) {
        
        boolean success = coinService.addCoin(userId, amount, operationType, description, relatedId);
        if (success) {
            return Result.success("积分记录添加成功");
        }
        return Result.error("积分记录添加失败");
    }

    /**
     * 用户签到获得积分
     * POST /api/coins/checkin/{userId}
     */
    @PostMapping("/checkin/{userId}")
    public Result<String> dailyCheckin(@PathVariable Long userId) {
        boolean success = coinService.signInReward(userId);
        if (success) {
            return Result.success("签到成功，获得积分奖励");
        }
        return Result.error("今日已签到或签到失败");
    }

    /**
     * 获取用户积分余额
     * GET /api/coins/balance/{userId}
     */
    @GetMapping("/balance/{userId}")
    public Result<Integer> getUserCoinBalance(@PathVariable Long userId) {
        Integer balance = coinService.getUserCoinBalance(userId);
        return Result.success(balance);
    }

    /**
     * 获取积分排行榜
     * GET /api/coins/leaderboard
     */
    @GetMapping("/leaderboard")
    public Result<List<UserCoin>> getCoinLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<UserCoin> leaderboard = coinService.getCoinBalanceRanking(limit);
        return Result.success(leaderboard);
    }

    /**
     * 获取积分统计信息
     * GET /api/coins/statistics
     */
    @GetMapping("/statistics")
    public Result<UserCoin> getCoinStatistics() {
        UserCoin statistics = coinService.getCoinStatistics();
        return Result.success(statistics);
    }

    /**
     * 发布文章奖励积分
     * POST /api/coins/publish-reward/{userId}
     */
    @PostMapping("/publish-reward/{userId}")
    public Result<String> publishPostReward(
            @PathVariable Long userId,
            @RequestParam Long postId) {
        
        boolean success = coinService.publishPostReward(userId, postId);
        if (success) {
            return Result.success("发布文章奖励积分成功");
        }
        return Result.error("奖励积分失败");
    }

    /**
     * 评论奖励积分
     * POST /api/coins/comment-reward/{userId}
     */
    @PostMapping("/comment-reward/{userId}")
    public Result<String> commentReward(
            @PathVariable Long userId,
            @RequestParam Long commentId) {
        
        boolean success = coinService.commentReward(userId, commentId);
        if (success) {
            return Result.success("评论奖励积分成功");
        }
        return Result.error("奖励积分失败");
    }

    /**
     * 消费积分
     * POST /api/coins/consume/{userId}
     */
    @PostMapping("/consume/{userId}")
    public Result<String> consumeCoin(
            @PathVariable Long userId,
            @RequestParam Integer amount,
            @RequestParam String description,
            @RequestParam(required = false) Long relatedId) {
        
        boolean success = coinService.consumeCoin(userId, amount, description, relatedId);
        if (success) {
            return Result.success("积分消费成功");
        }
        return Result.error("积分消费失败");
    }
}