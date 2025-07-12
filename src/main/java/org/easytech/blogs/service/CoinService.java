package org.easytech.blogs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.entity.Coin;
import org.easytech.blogs.entity.UserCoin;

import java.util.List;

/**
 * 积分服务接口
 * 提供积分相关的业务逻辑处理
 */
public interface CoinService {

    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param amount 积分数量
     * @param operationType 操作类型
     * @param description 操作描述
     * @param relatedId 关联对象ID
     * @return 操作结果
     */
    boolean addCoin(Long userId, Integer amount, Integer operationType, String description, Long relatedId);

    /**
     * 减少用户积分
     * @param userId 用户ID
     * @param amount 积分数量
     * @param operationType 操作类型
     * @param description 操作描述
     * @param relatedId 关联对象ID
     * @return 操作结果
     */
    boolean reduceCoin(Long userId, Integer amount, Integer operationType, String description, Long relatedId);

    /**
     * 获取用户积分余额
     * @param userId 用户ID
     * @return 积分余额
     */
    Integer getUserCoinBalance(Long userId);

    /**
     * 获取用户积分账户信息
     * @param userId 用户ID
     * @return 积分账户信息
     */
    UserCoin getUserCoinAccount(Long userId);

    /**
     * 初始化用户积分账户
     * @param userId 用户ID
     * @return 初始化结果
     */
    boolean initUserCoinAccount(Long userId);

    /**
     * 分页查询用户积分记录
     * @param page 分页参数
     * @param userId 用户ID
     * @return 积分记录分页列表
     */
    IPage<Coin> getUserCoinRecords(Page<Coin> page, Long userId);

    /**
     * 根据操作类型分页查询积分记录
     * @param page 分页参数
     * @param operationType 操作类型
     * @return 积分记录分页列表
     */
    IPage<Coin> getCoinRecordsByType(Page<Coin> page, Integer operationType);

    /**
     * 获取积分排行榜
     * @param limit 排行榜数量
     * @return 积分排行列表
     */
    List<Coin> getCoinRanking(Integer limit);

    /**
     * 获取积分余额排行榜
     * @param limit 排行榜数量
     * @return 积分余额排行列表
     */
    List<UserCoin> getCoinBalanceRanking(Integer limit);

    /**
     * 获取用户今日积分变动
     * @param userId 用户ID
     * @return 今日积分变动
     */
    Integer getTodayCoinChange(Long userId);

    /**
     * 检查用户今日是否已获得指定类型积分
     * @param userId 用户ID
     * @param operationType 操作类型
     * @param relatedId 关联对象ID
     * @return 是否已获得积分
     */
    boolean hasGainedCoinToday(Long userId, Integer operationType, Long relatedId);

    /**
     * 用户签到获得积分
     * @param userId 用户ID
     * @return 签到结果
     */
    boolean signInReward(Long userId);

    /**
     * 发布文章奖励积分
     * @param userId 用户ID
     * @param postId 文章ID
     * @return 奖励结果
     */
    boolean publishPostReward(Long userId, Long postId);

    /**
     * 评论奖励积分
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 奖励结果
     */
    boolean commentReward(Long userId, Long commentId);

    /**
     * 被点赞奖励积分
     * @param userId 用户ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @param targetId 目标ID
     * @return 奖励结果
     */
    boolean likedReward(Long userId, Integer targetType, Long targetId);

    /**
     * 消费积分
     * @param userId 用户ID
     * @param amount 消费数量
     * @param description 消费描述
     * @param relatedId 关联对象ID
     * @return 消费结果
     */
    boolean consumeCoin(Long userId, Integer amount, String description, Long relatedId);

    /**
     * 获取积分统计信息
     * @return 积分统计信息
     */
    UserCoin getCoinStatistics();

    /**
     * 转账积分（用户之间转移积分）
     * @param fromUserId 转出用户ID
     * @param toUserId 转入用户ID
     * @param amount 转账金额
     * @param description 转账描述
     * @return 转账结果
     */
    boolean transferCoin(Long fromUserId, Long toUserId, Integer amount, String description);
}