package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.Coin;
import org.easytech.blogs.entity.UserCoin;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.CoinMapper;
import org.easytech.blogs.mapper.UserCoinMapper;
import org.easytech.blogs.service.CoinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 积分服务实现类
 * 实现积分相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {

    private final CoinMapper coinMapper;
    private final UserCoinMapper userCoinMapper;

    // 积分操作类型常量
    private static final int OPERATION_TYPE_PUBLISH_POST = 1;    // 发布文章
    private static final int OPERATION_TYPE_COMMENT = 2;        // 评论
    private static final int OPERATION_TYPE_LIKE = 3;           // 点赞
    private static final int OPERATION_TYPE_LIKED = 4;          // 被点赞
    private static final int OPERATION_TYPE_SIGN_IN = 5;        // 签到
    private static final int OPERATION_TYPE_CONSUME = 6;        // 消费

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCoin(Long userId, Integer amount, Integer operationType, String description, Long relatedId) {
        if (userId == null || amount == null || amount <= 0 || operationType == null) {
            throw new ValidationException("参数不能为空，积分数量必须大于0");
        }

        try {
            // 确保用户积分账户存在
            ensureUserCoinAccount(userId);

            // 创建积分记录
            Coin coinRecord = new Coin();
            coinRecord.setUserId(userId);
            coinRecord.setAmount(amount);
            coinRecord.setOperationType(operationType);
            coinRecord.setDescription(StringUtils.hasText(description) ? description : "积分增加");
            coinRecord.setRelatedId(relatedId);

            int result = coinMapper.insert(coinRecord);
            if (result > 0) {
                // 更新用户积分余额
                userCoinMapper.increaseCoin(userId, amount);
                log.info("用户积分增加成功，用户ID: {}, 积分: {}, 操作类型: {}", userId, amount, operationType);
                return true;
            }
        } catch (Exception e) {
            log.error("用户积分增加失败，用户ID: {}, 积分: {}", userId, amount, e);
            throw new BusinessException("积分增加失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reduceCoin(Long userId, Integer amount, Integer operationType, String description, Long relatedId) {
        if (userId == null || amount == null || amount <= 0 || operationType == null) {
            throw new ValidationException("参数不能为空，积分数量必须大于0");
        }

        // 检查用户积分余额是否足够
        Integer currentBalance = getUserCoinBalance(userId);
        if (currentBalance < amount) {
            throw new BusinessException("积分余额不足");
        }

        try {
            // 创建积分记录（负数表示减少）
            Coin coinRecord = new Coin();
            coinRecord.setUserId(userId);
            coinRecord.setAmount(-amount);
            coinRecord.setOperationType(operationType);
            coinRecord.setDescription(StringUtils.hasText(description) ? description : "积分消费");
            coinRecord.setRelatedId(relatedId);

            int result = coinMapper.insert(coinRecord);
            if (result > 0) {
                // 更新用户积分余额
                int updateResult = userCoinMapper.decreaseCoin(userId, amount);
                if (updateResult == 0) {
                    throw new BusinessException("积分余额不足或用户不存在");
                }
                
                log.info("用户积分减少成功，用户ID: {}, 积分: {}, 操作类型: {}", userId, amount, operationType);
                return true;
            }
        } catch (Exception e) {
            log.error("用户积分减少失败，用户ID: {}, 积分: {}", userId, amount, e);
            throw new BusinessException("积分减少失败，请稍后重试");
        }

        return false;
    }

    @Override
    public Integer getUserCoinBalance(Long userId) {
        if (userId == null) {
            return 0;
        }

        UserCoin userCoin = userCoinMapper.selectByUserId(userId);
        return userCoin != null ? userCoin.getCoinBalance() : 0;
    }

    @Override
    public UserCoin getUserCoinAccount(Long userId) {
        if (userId == null) {
            return null;
        }
        return userCoinMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initUserCoinAccount(Long userId) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        // 检查是否已存在积分账户
        UserCoin existAccount = userCoinMapper.selectByUserId(userId);
        if (existAccount != null) {
            return true; // 账户已存在
        }

        try {
            UserCoin userCoin = new UserCoin();
            userCoin.setUserId(userId);
            userCoin.setCoinBalance(0);
            userCoin.setTotalEarned(0);
            userCoin.setTotalConsumed(0);

            int result = userCoinMapper.insert(userCoin);
            if (result > 0) {
                log.info("用户积分账户初始化成功，用户ID: {}", userId);
                return true;
            }
        } catch (Exception e) {
            log.error("用户积分账户初始化失败，用户ID: {}", userId, e);
            throw new BusinessException("积分账户初始化失败");
        }

        return false;
    }

    @Override
    public IPage<Coin> getUserCoinRecords(Page<Coin> page, Long userId) {
        if (userId == null) {
            return page;
        }
        return coinMapper.selectCoinRecordsByUserId(page, userId);
    }

    @Override
    public IPage<Coin> getCoinRecordsByType(Page<Coin> page, Integer operationType) {
        if (operationType == null) {
            return page;
        }
        return coinMapper.selectCoinRecordsByType(page, operationType);
    }

    @Override
    public List<Coin> getCoinRanking(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return coinMapper.selectCoinRanking(limit);
    }

    @Override
    public List<UserCoin> getCoinBalanceRanking(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return userCoinMapper.selectCoinBalanceRanking(limit);
    }

    @Override
    public Integer getTodayCoinChange(Long userId) {
        if (userId == null) {
            return 0;
        }

        LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX);

        return coinMapper.sumTodayCoin(userId, startTime, endTime);
    }

    @Override
    public boolean hasGainedCoinToday(Long userId, Integer operationType, Long relatedId) {
        if (userId == null || operationType == null) {
            return false;
        }

        LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX);

        return coinMapper.hasGainedCoinToday(userId, operationType, relatedId, startTime, endTime);
    }

    @Override
    public boolean signInReward(Long userId) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        // 检查今日是否已签到
        if (hasGainedCoinToday(userId, OPERATION_TYPE_SIGN_IN, null)) {
            throw new BusinessException("今日已签到");
        }

        // 签到奖励积分（可配置）
        int signInReward = 10;
        return addCoin(userId, signInReward, OPERATION_TYPE_SIGN_IN, "每日签到奖励", null);
    }

    @Override
    public boolean publishPostReward(Long userId, Long postId) {
        if (userId == null || postId == null) {
            throw new ValidationException("用户ID和文章ID不能为空");
        }

        // 检查今日发布文章是否已获得奖励
        if (hasGainedCoinToday(userId, OPERATION_TYPE_PUBLISH_POST, postId)) {
            return true; // 已获得奖励
        }

        // 发布文章奖励积分（可配置）
        int publishReward = 20;
        return addCoin(userId, publishReward, OPERATION_TYPE_PUBLISH_POST, "发布文章奖励", postId);
    }

    @Override
    public boolean commentReward(Long userId, Long commentId) {
        if (userId == null || commentId == null) {
            throw new ValidationException("用户ID和评论ID不能为空");
        }

        // 检查今日评论是否已获得奖励
        if (hasGainedCoinToday(userId, OPERATION_TYPE_COMMENT, commentId)) {
            return true; // 已获得奖励
        }

        // 评论奖励积分（可配置）
        int commentReward = 5;
        return addCoin(userId, commentReward, OPERATION_TYPE_COMMENT, "发表评论奖励", commentId);
    }

    @Override
    public boolean likedReward(Long userId, Integer targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) {
            throw new ValidationException("参数不能为空");
        }

        // 检查今日被点赞是否已获得奖励
        if (hasGainedCoinToday(userId, OPERATION_TYPE_LIKED, targetId)) {
            return true; // 已获得奖励
        }

        // 被点赞奖励积分（可配置）
        int likedReward = 2;
        String description = targetType == 1 ? "文章被点赞奖励" : "评论被点赞奖励";
        return addCoin(userId, likedReward, OPERATION_TYPE_LIKED, description, targetId);
    }

    @Override
    public boolean consumeCoin(Long userId, Integer amount, String description, Long relatedId) {
        if (userId == null || amount == null || amount <= 0) {
            throw new ValidationException("参数不能为空，消费金额必须大于0");
        }

        return reduceCoin(userId, amount, OPERATION_TYPE_CONSUME, description, relatedId);
    }

    @Override
    public UserCoin getCoinStatistics() {
        return userCoinMapper.getCoinStatistics();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferCoin(Long fromUserId, Long toUserId, Integer amount, String description) {
        if (fromUserId == null || toUserId == null || amount == null || amount <= 0) {
            throw new ValidationException("参数不能为空，转账金额必须大于0");
        }

        if (fromUserId.equals(toUserId)) {
            throw new ValidationException("不能给自己转账");
        }

        // 检查转出用户积分余额
        Integer fromBalance = getUserCoinBalance(fromUserId);
        if (fromBalance < amount) {
            throw new BusinessException("积分余额不足");
        }

        try {
            // 确保接收方积分账户存在
            ensureUserCoinAccount(toUserId);

            String transferDescription = StringUtils.hasText(description) ? description : "积分转账";

            // 转出积分
            boolean deductResult = reduceCoin(fromUserId, amount, OPERATION_TYPE_CONSUME, 
                "转出积分给用户" + toUserId + "：" + transferDescription, toUserId);

            if (deductResult) {
                // 转入积分
                boolean addResult = addCoin(toUserId, amount, OPERATION_TYPE_CONSUME, 
                    "接收用户" + fromUserId + "转入积分：" + transferDescription, fromUserId);

                if (addResult) {
                    log.info("积分转账成功，从用户{}转给用户{}，金额: {}", fromUserId, toUserId, amount);
                    return true;
                } else {
                    // 转入失败，回滚转出操作
                    addCoin(fromUserId, amount, OPERATION_TYPE_CONSUME, "积分转账失败回滚", toUserId);
                    throw new BusinessException("积分转账失败");
                }
            }
        } catch (Exception e) {
            log.error("积分转账失败，从用户{}转给用户{}，金额: {}", fromUserId, toUserId, amount, e);
            throw new BusinessException("积分转账失败，请稍后重试");
        }

        return false;
    }

    /**
     * 确保用户积分账户存在
     */
    private void ensureUserCoinAccount(Long userId) {
        UserCoin userCoin = userCoinMapper.selectByUserId(userId);
        if (userCoin == null) {
            initUserCoinAccount(userId);
        }
    }
}