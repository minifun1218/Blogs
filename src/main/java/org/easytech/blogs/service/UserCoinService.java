package org.easytech.blogs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.entity.UserCoin;

import java.util.List;
import java.util.Map;

/**
 * 用户积分账户服务接口
 * 提供用户积分账户相关的业务逻辑处理
 */
public interface UserCoinService {

    /**
     * 创建用户积分账户
     * @param userCoin 用户积分账户信息
     * @return 创建结果
     */
    boolean createUserCoinAccount(UserCoin userCoin);

    /**
     * 自动创建用户积分账户
     * @param userId 用户ID
     * @return 创建的积分账户
     */
    UserCoin createUserCoinAccount(Long userId);

    /**
     * 更新用户积分账户
     * @param userCoin 用户积分账户信息
     * @return 更新结果
     */
    boolean updateUserCoinAccount(UserCoin userCoin);

    /**
     * 删除用户积分账户
     * @param id 账户ID
     * @return 删除结果
     */
    boolean deleteUserCoinAccount(Long id);

    /**
     * 根据ID获取用户积分账户
     * @param id 账户ID
     * @return 用户积分账户
     */
    UserCoin getUserCoinById(Long id);

    /**
     * 根据用户ID获取积分账户
     * @param userId 用户ID
     * @return 用户积分账户
     */
    UserCoin getUserCoinByUserId(Long userId);

    /**
     * 分页查询用户积分账户
     * @param page 分页参数
     * @param userId 用户ID，null表示查询所有
     * @param orderBy 排序字段
     * @return 分页结果
     */
    IPage<UserCoin> getUserCoinPage(Page<UserCoin> page, Long userId, String orderBy);

    /**
     * 获取积分排行榜
     * @param limit 排行榜数量
     * @param orderBy 排序字段（coinBalance/totalEarned/totalConsumed）
     * @return 排行榜列表
     */
    List<Map<String, Object>> getCoinLeaderboard(Integer limit, String orderBy);

    /**
     * 获取积分统计信息
     * @return 积分统计信息
     */
    Map<String, Object> getCoinStatistics();

    /**
     * 获取积分分布统计
     * @return 积分分布统计
     */
    List<Map<String, Object>> getCoinDistribution();

    /**
     * 调整用户积分余额
     * @param id 账户ID
     * @param amount 调整金额（正数增加，负数减少）
     * @param reason 调整原因
     * @return 调整结果
     */
    boolean adjustBalance(Long id, Integer amount, String reason);

    /**
     * 重新计算用户积分余额
     * @param id 账户ID
     * @return 重新计算结果
     */
    boolean recalculateBalance(Long id);

    /**
     * 批量重新计算所有用户积分余额
     * @return 处理的账户数量
     */
    int recalculateAllBalances();

    /**
     * 冻结或解冻积分账户
     * @param id 账户ID
     * @param freeze 是否冻结
     * @return 操作结果
     */
    boolean freezeAccount(Long id, boolean freeze);

    /**
     * 获取用户积分变化趋势
     * @param id 账户ID
     * @param days 天数
     * @return 变化趋势数据
     */
    List<Map<String, Object>> getUserCoinTrend(Long id, Integer days);

    /**
     * 检查用户是否已有积分账户
     * @param userId 用户ID
     * @return 是否存在积分账户
     */
    boolean hasUserCoinAccount(Long userId);

    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param amount 积分数量
     * @param operationType 操作类型
     * @param description 操作描述
     * @return 操作结果
     */
    boolean addUserCoin(Long userId, Integer amount, Integer operationType, String description);

    /**
     * 减少用户积分
     * @param userId 用户ID
     * @param amount 积分数量
     * @param operationType 操作类型
     * @param description 操作描述
     * @return 操作结果
     */
    boolean reduceUserCoin(Long userId, Integer amount, Integer operationType, String description);

    /**
     * 获取用户积分余额
     * @param userId 用户ID
     * @return 积分余额
     */
    Integer getUserCoinBalance(Long userId);

    /**
     * 初始化所有用户的积分账户
     * @return 初始化结果
     */
    boolean initAllUserCoinAccounts();
}