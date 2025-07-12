package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.easytech.blogs.entity.UserCoin;

/**
 * 用户积分账户Mapper接口
 * 负责用户积分账户信息的数据访问操作
 */
@Mapper
public interface UserCoinMapper extends BaseMapper<UserCoin> {

    /**
     * 根据用户ID查询积分账户
     * @param userId 用户ID
     * @return 用户积分账户信息
     */
    @Select("SELECT * FROM tb_user_coin WHERE user_id = #{userId}")
    UserCoin selectByUserId(@Param("userId") Long userId);

    /**
     * 更新用户积分余额
     * @param userId 用户ID
     * @param coinBalance 积分余额
     * @return 更新影响行数
     */
    @Update("UPDATE tb_user_coin SET coin_balance = #{coinBalance}, update_time = NOW() WHERE user_id = #{userId}")
    int updateCoinBalance(@Param("userId") Long userId, @Param("coinBalance") Integer coinBalance);

    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param amount 增加的积分数量
     * @return 更新影响行数
     */
    @Update("UPDATE tb_user_coin SET coin_balance = coin_balance + #{amount}, " +
            "total_earned = total_earned + #{amount}, update_time = NOW() " +
            "WHERE user_id = #{userId}")
    int increaseCoin(@Param("userId") Long userId, @Param("amount") Integer amount);

    /**
     * 减少用户积分
     * @param userId 用户ID
     * @param amount 减少的积分数量
     * @return 更新影响行数
     */
    @Update("UPDATE tb_user_coin SET coin_balance = coin_balance - #{amount}, " +
            "total_consumed = total_consumed + #{amount}, update_time = NOW() " +
            "WHERE user_id = #{userId} AND coin_balance >= #{amount}")
    int decreaseCoin(@Param("userId") Long userId, @Param("amount") Integer amount);

    /**
     * 查询积分余额排行榜
     * @param limit 排行榜数量限制
     * @return 积分余额排行列表
     */
    @Select("SELECT uc.*, u.username, u.nickname, u.avatar " +
            "FROM tb_user_coin uc " +
            "LEFT JOIN tb_user u ON uc.user_id = u.id " +
            "WHERE u.status = 1 AND u.is_deleted = 0 " +
            "ORDER BY uc.coin_balance DESC " +
            "LIMIT #{limit}")
    java.util.List<UserCoin> selectCoinBalanceRanking(@Param("limit") Integer limit);

    /**
     * 统计总积分情况
     * @return 积分统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as user_count, " +
            "SUM(coin_balance) as total_balance, " +
            "SUM(total_earned) as total_earned, " +
            "SUM(total_consumed) as total_consumed, " +
            "AVG(coin_balance) as avg_balance " +
            "FROM tb_user_coin uc " +
            "LEFT JOIN tb_user u ON uc.user_id = u.id " +
            "WHERE u.status = 1 AND u.is_deleted = 0")
    UserCoin getCoinStatistics();
}