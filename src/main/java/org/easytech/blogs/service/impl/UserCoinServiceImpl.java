package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.UserCoin;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.UserCoinMapper;
import org.easytech.blogs.service.CoinService;
import org.easytech.blogs.service.UserCoinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户积分账户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCoinServiceImpl extends ServiceImpl<UserCoinMapper, UserCoin> implements UserCoinService {

    private final UserCoinMapper userCoinMapper;
    private final CoinService coinService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserCoinAccount(UserCoin userCoin) {
        if (userCoin == null || userCoin.getUserId() == null) {
            throw new ValidationException("用户ID不能为空");
        }
        
        // 检查是否已存在
        if (hasUserCoinAccount(userCoin.getUserId())) {
            throw new BusinessException("用户积分账户已存在");
        }
        
        // 设置默认值
        if (userCoin.getCoinBalance() == null) {
            userCoin.setCoinBalance(0);
        }
        if (userCoin.getTotalEarned() == null) {
            userCoin.setTotalEarned(0);
        }
        if (userCoin.getTotalConsumed() == null) {
            userCoin.setTotalConsumed(0);
        }
        
        return save(userCoin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCoin createUserCoinAccount(Long userId) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }
        
        UserCoin userCoin = new UserCoin();
        userCoin.setUserId(userId);
        userCoin.setCoinBalance(0);
        userCoin.setTotalEarned(0);
        userCoin.setTotalConsumed(0);
        
        boolean success = save(userCoin);
        if (success) {
            return userCoin;
        }
        throw new BusinessException("创建用户积分账户失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserCoinAccount(UserCoin userCoin) {
        if (userCoin == null || userCoin.getId() == null) {
            throw new ValidationException("积分账户信息不能为空");
        }
        return updateById(userCoin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserCoinAccount(Long id) {
        if (id == null) {
            throw new ValidationException("账户ID不能为空");
        }
        return removeById(id);
    }

    @Override
    public UserCoin getUserCoinById(Long id) {
        if (id == null) {
            return null;
        }
        return getById(id);
    }

    @Override
    public UserCoin getUserCoinByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        
        QueryWrapper<UserCoin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return getOne(queryWrapper);
    }

    @Override
    public IPage<UserCoin> getUserCoinPage(Page<UserCoin> page, Long userId, String orderBy) {
        QueryWrapper<UserCoin> queryWrapper = new QueryWrapper<>();
        
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        
        // 设置排序
        if ("coinBalance".equals(orderBy)) {
            queryWrapper.orderByDesc("coin_balance");
        } else if ("totalEarned".equals(orderBy)) {
            queryWrapper.orderByDesc("total_earned");
        } else if ("totalConsumed".equals(orderBy)) {
            queryWrapper.orderByDesc("total_consumed");
        } else {
            queryWrapper.orderByDesc("update_time");
        }
        
        return page(page, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getCoinLeaderboard(Integer limit, String orderBy) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        QueryWrapper<UserCoin> queryWrapper = new QueryWrapper<>();
        
        // 设置排序
        if ("totalEarned".equals(orderBy)) {
            queryWrapper.orderByDesc("total_earned");
        } else if ("totalConsumed".equals(orderBy)) {
            queryWrapper.orderByDesc("total_consumed");
        } else {
            queryWrapper.orderByDesc("coin_balance");
        }
        
        queryWrapper.last("LIMIT " + limit);
        
        List<UserCoin> userCoins = list(queryWrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (int i = 0; i < userCoins.size(); i++) {
            UserCoin userCoin = userCoins.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i + 1);
            item.put("userId", userCoin.getUserId());
            item.put("coinBalance", userCoin.getCoinBalance());
            item.put("totalEarned", userCoin.getTotalEarned());
            item.put("totalConsumed", userCoin.getTotalConsumed());
            result.add(item);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getCoinStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 总用户数
            long totalUsers = count();
            statistics.put("totalUsers", totalUsers);
            
            // 总积分余额
            QueryWrapper<UserCoin> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("IFNULL(SUM(coin_balance), 0) as totalBalance");
            List<Map<String, Object>> balanceResult = listMaps(queryWrapper);
            if (!balanceResult.isEmpty()) {
                statistics.put("totalBalance", balanceResult.get(0).get("totalBalance"));
            } else {
                statistics.put("totalBalance", 0);
            }
            
            // 总获得积分
            queryWrapper = new QueryWrapper<>();
            queryWrapper.select("IFNULL(SUM(total_earned), 0) as totalEarned");
            List<Map<String, Object>> earnedResult = listMaps(queryWrapper);
            if (!earnedResult.isEmpty()) {
                statistics.put("totalEarned", earnedResult.get(0).get("totalEarned"));
            } else {
                statistics.put("totalEarned", 0);
            }
            
            // 总消费积分
            queryWrapper = new QueryWrapper<>();
            queryWrapper.select("IFNULL(SUM(total_consumed), 0) as totalConsumed");
            List<Map<String, Object>> consumedResult = listMaps(queryWrapper);
            if (!consumedResult.isEmpty()) {
                statistics.put("totalConsumed", consumedResult.get(0).get("totalConsumed"));
            } else {
                statistics.put("totalConsumed", 0);
            }
            
        } catch (Exception e) {
            log.error("获取积分统计信息失败", e);
            statistics.put("totalUsers", 0);
            statistics.put("totalBalance", 0);
            statistics.put("totalEarned", 0);
            statistics.put("totalConsumed", 0);
        }
        
        return statistics;
    }

    @Override
    public List<Map<String, Object>> getCoinDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        try {
            // 积分区间分布
            String[] ranges = {"0", "1-100", "101-500", "501-1000", "1001-5000", "5000+"};
            String[] conditions = {
                "coin_balance = 0",
                "coin_balance BETWEEN 1 AND 100",
                "coin_balance BETWEEN 101 AND 500", 
                "coin_balance BETWEEN 501 AND 1000",
                "coin_balance BETWEEN 1001 AND 5000",
                "coin_balance > 5000"
            };
            
            for (int i = 0; i < ranges.length; i++) {
                QueryWrapper<UserCoin> queryWrapper = new QueryWrapper<>();
                queryWrapper.apply(conditions[i]);
                long count = count(queryWrapper);
                
                Map<String, Object> item = new HashMap<>();
                item.put("range", ranges[i]);
                item.put("count", count);
                distribution.add(item);
            }
            
        } catch (Exception e) {
            log.error("获取积分分布统计失败", e);
        }
        
        return distribution;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustBalance(Long id, Integer amount, String reason) {
        if (id == null || amount == null || amount == 0) {
            throw new ValidationException("参数不能为空，调整金额不能为0");
        }
        
        UserCoin userCoin = getById(id);
        if (userCoin == null) {
            throw new BusinessException("积分账户不存在");
        }
        
        // 检查余额是否足够（如果是减少积分）
        if (amount < 0 && userCoin.getCoinBalance() + amount < 0) {
            throw new BusinessException("积分余额不足");
        }
        
        // 更新余额
        userCoin.setCoinBalance(userCoin.getCoinBalance() + amount);
        
        // 更新统计
        if (amount > 0) {
            userCoin.setTotalEarned(userCoin.getTotalEarned() + amount);
        } else {
            userCoin.setTotalConsumed(userCoin.getTotalConsumed() + Math.abs(amount));
        }
        
        boolean success = updateById(userCoin);
        
        // 记录积分变化
        if (success) {
            try {
                int operationType = amount > 0 ? 1 : 6; // 1-增加，6-消费
                coinService.addCoin(userCoin.getUserId(), amount, operationType, reason, null);
            } catch (Exception e) {
                log.error("记录积分变化失败", e);
            }
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recalculateBalance(Long id) {
        // 这里应该根据积分记录重新计算余额
        // 由于没有具体的积分记录查询逻辑，暂时返回true
        log.info("重新计算用户积分余额: {}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int recalculateAllBalances() {
        List<UserCoin> allAccounts = list();
        int count = 0;
        
        for (UserCoin account : allAccounts) {
            try {
                if (recalculateBalance(account.getId())) {
                    count++;
                }
            } catch (Exception e) {
                log.error("重新计算用户{}积分余额失败", account.getUserId(), e);
            }
        }
        
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeAccount(Long id, boolean freeze) {
        // 这里应该添加冻结状态字段，目前暂时返回true
        log.info("{}积分账户: {}", freeze ? "冻结" : "解冻", id);
        return true;
    }

    @Override
    public List<Map<String, Object>> getUserCoinTrend(Long id, Integer days) {
        // 这里应该查询积分变化趋势，暂时返回空列表
        log.info("获取用户{}最近{}天的积分变化趋势", id, days);
        return new ArrayList<>();
    }

    @Override
    public boolean hasUserCoinAccount(Long userId) {
        if (userId == null) {
            return false;
        }
        
        QueryWrapper<UserCoin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return count(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUserCoin(Long userId, Integer amount, Integer operationType, String description) {
        if (userId == null || amount == null || amount <= 0) {
            throw new ValidationException("参数不能为空，积分数量必须大于0");
        }
        
        // 确保用户有积分账户
        UserCoin userCoin = getUserCoinByUserId(userId);
        if (userCoin == null) {
            userCoin = createUserCoinAccount(userId);
        }
        
        // 更新积分账户
        userCoin.setCoinBalance(userCoin.getCoinBalance() + amount);
        userCoin.setTotalEarned(userCoin.getTotalEarned() + amount);
        
        boolean success = updateById(userCoin);
        
        // 记录积分变化
        if (success) {
            try {
                coinService.addCoin(userId, amount, operationType, description, null);
            } catch (Exception e) {
                log.error("记录积分变化失败", e);
            }
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reduceUserCoin(Long userId, Integer amount, Integer operationType, String description) {
        if (userId == null || amount == null || amount <= 0) {
            throw new ValidationException("参数不能为空，积分数量必须大于0");
        }
        
        UserCoin userCoin = getUserCoinByUserId(userId);
        if (userCoin == null) {
            throw new BusinessException("用户积分账户不存在");
        }
        
        // 检查余额是否足够
        if (userCoin.getCoinBalance() < amount) {
            throw new BusinessException("积分余额不足");
        }
        
        // 更新积分账户
        userCoin.setCoinBalance(userCoin.getCoinBalance() - amount);
        userCoin.setTotalConsumed(userCoin.getTotalConsumed() + amount);
        
        boolean success = updateById(userCoin);
        
        // 记录积分变化
        if (success) {
            try {
                coinService.reduceCoin(userId, amount, operationType, description, null);
            } catch (Exception e) {
                log.error("记录积分变化失败", e);
            }
        }
        
        return success;
    }

    @Override
    public Integer getUserCoinBalance(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        UserCoin userCoin = getUserCoinByUserId(userId);
        return userCoin != null ? userCoin.getCoinBalance() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initAllUserCoinAccounts() {
        // 这里应该为所有没有积分账户的用户创建账户
        // 由于需要查询用户表，暂时返回true
        log.info("初始化所有用户的积分账户");
        return true;
    }
}