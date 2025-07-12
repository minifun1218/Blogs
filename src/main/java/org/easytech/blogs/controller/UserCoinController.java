package org.easytech.blogs.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.common.PageResult;
import org.easytech.blogs.common.Result;
import org.easytech.blogs.entity.UserCoin;
import org.easytech.blogs.service.UserCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户积分账户控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/user-coins")
@CrossOrigin
@Validated
public class UserCoinController {

    private final UserCoinService userCoinService;

    @Autowired
    public UserCoinController(UserCoinService userCoinService) {
        this.userCoinService = userCoinService;
    }

    /**
     * 分页查询用户积分账户
     * GET /api/user-coins?page=1&size=10&userId=1
     */
    @GetMapping
    public Result<PageResult<UserCoin>> getUserCoins(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String orderBy) {
        
        Page<UserCoin> pageInfo = new Page<>(page, size);
        IPage<UserCoin> result = userCoinService.getUserCoinPage(pageInfo, userId, orderBy);
        
        return Result.success(PageResult.of(result));
    }

    /**
     * 根据用户ID查询积分账户
     * GET /api/user-coins/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result<UserCoin> getUserCoinByUserId(@PathVariable Long userId) {
        UserCoin userCoin = userCoinService.getUserCoinByUserId(userId);
        if (userCoin == null) {
            // 如果用户没有积分账户，自动创建一个
            userCoin = userCoinService.createUserCoinAccount(userId);
        }
        return Result.success(userCoin);
    }

    /**
     * 根据ID查询积分账户详情
     * GET /api/user-coins/{id}
     */
    @GetMapping("/{id}")
    public Result<UserCoin> getUserCoinById(@PathVariable Long id) {
        UserCoin userCoin = userCoinService.getUserCoinById(id);
        if (userCoin == null) {
            return Result.notFound("积分账户不存在");
        }
        return Result.success(userCoin);
    }

    /**
     * 创建用户积分账户
     * POST /api/user-coins
     */
    @PostMapping
    public Result<UserCoin> createUserCoin(@Validated @RequestBody UserCoin userCoin) {
        // 检查用户是否已有积分账户
        if (userCoinService.hasUserCoinAccount(userCoin.getUserId())) {
            return Result.badRequest("用户已有积分账户");
        }
        
        boolean success = userCoinService.createUserCoinAccount(userCoin);
        if (success) {
            return Result.success("积分账户创建成功", userCoin);
        }
        return Result.error("积分账户创建失败");
    }

    /**
     * 更新积分账户信息（管理员功能）
     * PUT /api/user-coins/{id}
     */
    @PutMapping("/{id}")
    public Result<UserCoin> updateUserCoin(@PathVariable Long id, @Validated @RequestBody UserCoin userCoin) {
        UserCoin existingUserCoin = userCoinService.getUserCoinById(id);
        if (existingUserCoin == null) {
            return Result.notFound("积分账户不存在");
        }
        
        userCoin.setId(id);
        boolean success = userCoinService.updateUserCoinAccount(userCoin);
        if (success) {
            return Result.success("积分账户更新成功", userCoin);
        }
        return Result.error("积分账户更新失败");
    }

    /**
     * 调整用户积分余额（管理员功能）
     * PUT /api/user-coins/{id}/balance
     */
    @PutMapping("/{id}/balance")
    public Result<String> adjustBalance(
            @PathVariable Long id,
            @RequestParam Integer amount,
            @RequestParam String reason) {
        
        boolean success = userCoinService.adjustBalance(id, amount, reason);
        if (success) {
            return Result.success("积分余额调整成功");
        }
        return Result.error("积分余额调整失败");
    }

    /**
     * 获取积分排行榜
     * GET /api/user-coins/leaderboard
     */
    @GetMapping("/leaderboard")
    public Result<List<Map<String, Object>>> getCoinLeaderboard(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "coinBalance") String orderBy) {
        
        List<Map<String, Object>> leaderboard = userCoinService.getCoinLeaderboard(limit, orderBy);
        return Result.success(leaderboard);
    }

    /**
     * 获取积分统计信息
     * GET /api/user-coins/statistics
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getCoinStatistics() {
        Map<String, Object> statistics = userCoinService.getCoinStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取积分分布统计
     * GET /api/user-coins/distribution
     */
    @GetMapping("/distribution")
    public Result<List<Map<String, Object>>> getCoinDistribution() {
        List<Map<String, Object>> distribution = userCoinService.getCoinDistribution();
        return Result.success(distribution);
    }

    /**
     * 重新计算用户积分余额（管理员功能）
     * POST /api/user-coins/{id}/recalculate
     */
    @PostMapping("/{id}/recalculate")
    public Result<String> recalculateBalance(@PathVariable Long id) {
        boolean success = userCoinService.recalculateBalance(id);
        if (success) {
            return Result.success("积分余额重新计算成功");
        }
        return Result.error("积分余额重新计算失败");
    }

    /**
     * 批量重新计算所有用户积分余额（管理员功能）
     * POST /api/user-coins/recalculate-all
     */
    @PostMapping("/recalculate-all")
    public Result<String> recalculateAllBalances() {
        int count = userCoinService.recalculateAllBalances();
        return Result.success("批量重新计算完成，处理了 " + count + " 个账户");
    }

    /**
     * 冻结或解冻积分账户（管理员功能）
     * PUT /api/user-coins/{id}/freeze
     */
    @PutMapping("/{id}/freeze")
    public Result<String> freezeAccount(@PathVariable Long id, @RequestParam boolean freeze) {
        boolean success = userCoinService.freezeAccount(id, freeze);
        if (success) {
            String message = freeze ? "积分账户已冻结" : "积分账户已解冻";
            return Result.success(message);
        }
        return Result.error("操作失败");
    }

    /**
     * 删除积分账户（管理员功能，慎用）
     * DELETE /api/user-coins/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUserCoin(@PathVariable Long id) {
        UserCoin userCoin = userCoinService.getUserCoinById(id);
        if (userCoin == null) {
            return Result.notFound("积分账户不存在");
        }
        
        // 检查账户是否还有余额
        if (userCoin.getCoinBalance() > 0) {
            return Result.badRequest("账户还有积分余额，无法删除");
        }
        
        boolean success = userCoinService.deleteUserCoinAccount(id);
        if (success) {
            return Result.success("积分账户删除成功");
        }
        return Result.error("积分账户删除失败");
    }

    /**
     * 获取用户积分变化趋势
     * GET /api/user-coins/{id}/trend
     */
    @GetMapping("/{id}/trend")
    public Result<List<Map<String, Object>>> getUserCoinTrend(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days) {
        
        List<Map<String, Object>> trend = userCoinService.getUserCoinTrend(id, days);
        return Result.success(trend);
    }

    /**
     * 获取用户积分余额
     * GET /api/user-coins/balance/{userId}
     */
    @GetMapping("/balance/{userId}")
    public Result<Integer> getUserCoinBalance(@PathVariable Long userId) {
        Integer balance = userCoinService.getUserCoinBalance(userId);
        return Result.success(balance);
    }

    /**
     * 增加用户积分
     * POST /api/user-coins/add/{userId}
     */
    @PostMapping("/add/{userId}")
    public Result<String> addUserCoin(
            @PathVariable Long userId,
            @RequestParam Integer amount,
            @RequestParam Integer operationType,
            @RequestParam String description) {
        
        boolean success = userCoinService.addUserCoin(userId, amount, operationType, description);
        if (success) {
            return Result.success("积分增加成功");
        }
        return Result.error("积分增加失败");
    }

    /**
     * 减少用户积分
     * POST /api/user-coins/reduce/{userId}
     */
    @PostMapping("/reduce/{userId}")
    public Result<String> reduceUserCoin(
            @PathVariable Long userId,
            @RequestParam Integer amount,
            @RequestParam Integer operationType,
            @RequestParam String description) {
        
        boolean success = userCoinService.reduceUserCoin(userId, amount, operationType, description);
        if (success) {
            return Result.success("积分减少成功");
        }
        return Result.error("积分减少失败");
    }

    /**
     * 初始化所有用户的积分账户
     * POST /api/user-coins/init-all
     */
    @PostMapping("/init-all")
    public Result<String> initAllUserCoinAccounts() {
        boolean success = userCoinService.initAllUserCoinAccounts();
        if (success) {
            return Result.success("所有用户积分账户初始化成功");
        }
        return Result.error("积分账户初始化失败");
    }
}