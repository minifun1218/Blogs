package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.easytech.blogs.entity.Coin;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分记录Mapper接口
 * 负责积分变动记录的数据访问操作
 */
public interface CoinMapper extends BaseMapper<Coin> {

    /**
     * 分页查询用户积分记录
     * @param page 分页对象
     * @param userId 用户ID
     * @return 积分记录分页列表
     */
    @Select("SELECT * FROM tb_coin WHERE user_id = #{userId} ORDER BY create_time DESC")
    IPage<Coin> selectCoinRecordsByUserId(Page<Coin> page, @Param("userId") Long userId);

    /**
     * 根据操作类型查询积分记录
     * @param page 分页对象
     * @param operationType 操作类型
     * @return 积分记录分页列表
     */
    @Select("SELECT c.*, u.username, u.nickname " +
            "FROM tb_coin c " +
            "LEFT JOIN tb_user u ON c.user_id = u.id " +
            "WHERE c.operation_type = #{operationType} " +
            "ORDER BY c.create_time DESC")
    IPage<Coin> selectCoinRecordsByType(Page<Coin> page, @Param("operationType") Integer operationType);

    /**
     * 统计用户总积分
     * @param userId 用户ID
     * @return 用户总积分
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM tb_coin WHERE user_id = #{userId}")
    Integer sumCoinByUserId(@Param("userId") Long userId);

    /**
     * 查询用户今日积分变动
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 今日积分变动总额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM tb_coin " +
            "WHERE user_id = #{userId} AND create_time BETWEEN #{startTime} AND #{endTime}")
    Integer sumTodayCoin(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询积分排行榜
     * @param limit 排行榜数量限制
     * @return 用户积分排行列表
     */
    @Select("SELECT c.user_id, u.username, u.nickname, u.avatar, SUM(c.amount) as total_coin " +
            "FROM tb_coin c " +
            "LEFT JOIN tb_user u ON c.user_id = u.id " +
            "WHERE u.status = 1 AND u.is_deleted = 0 " +
            "GROUP BY c.user_id, u.username, u.nickname, u.avatar " +
            "ORDER BY total_coin DESC " +
            "LIMIT #{limit}")
    List<Coin> selectCoinRanking(@Param("limit") Integer limit);

    /**
     * 统计指定时间段内的积分变动
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 积分变动统计
     */
    @Select("SELECT operation_type, COUNT(*) as record_count, SUM(amount) as total_amount " +
            "FROM tb_coin " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY operation_type " +
            "ORDER BY operation_type")
    List<Coin> statisticsCoinByPeriod(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 检查用户今日是否已获得指定类型积分
     * @param userId 用户ID
     * @param operationType 操作类型
     * @param relatedId 关联对象ID
     * @param startTime 今日开始时间
     * @param endTime 今日结束时间
     * @return 是否已获得积分
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_coin " +
            "WHERE user_id = #{userId} AND operation_type = #{operationType} " +
            "AND related_id = #{relatedId} " +
            "AND create_time BETWEEN #{startTime} AND #{endTime}")
    boolean hasGainedCoinToday(@Param("userId") Long userId, 
                              @Param("operationType") Integer operationType, 
                              @Param("relatedId") Long relatedId,
                              @Param("startTime") LocalDateTime startTime, 
                              @Param("endTime") LocalDateTime endTime);
}