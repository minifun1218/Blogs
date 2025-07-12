package org.easytech.blogs.service;

import org.easytech.blogs.entity.LikeRecord;

import java.util.List;

/**
 * 点赞记录服务接口
 * 提供点赞记录相关的业务逻辑处理
 */
public interface LikeRecordService {

    /**
     * 点赞操作
     * @param userId 用户ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @param targetId 目标ID
     * @return 操作结果
     */
    boolean like(Long userId, Integer targetType, Long targetId);

    /**
     * 取消点赞操作
     * @param userId 用户ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @param targetId 目标ID
     * @return 操作结果
     */
    boolean unlike(Long userId, Integer targetType, Long targetId);

    /**
     * 切换点赞状态
     * @param userId 用户ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @param targetId 目标ID
     * @return 当前点赞状态（true-已点赞，false-未点赞）
     */
    boolean toggleLike(Long userId, Integer targetType, Long targetId);

    /**
     * 检查用户是否已点赞
     * @param userId 用户ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @param targetId 目标ID
     * @return 是否已点赞
     */
    boolean hasLiked(Long userId, Integer targetType, Long targetId);

    /**
     * 统计目标的点赞数
     * @param targetType 目标类型（1-文章，2-评论）
     * @param targetId 目标ID
     * @return 点赞数量
     */
    Long countLikes(Integer targetType, Long targetId);

    /**
     * 获取用户点赞的目标列表
     * @param userId 用户ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @return 目标ID列表
     */
    List<Long> getUserLikedTargets(Long userId, Integer targetType);

    /**
     * 根据ID获取点赞记录
     * @param recordId 记录ID
     * @return 点赞记录
     */
    LikeRecord getLikeRecordById(Long recordId);

    /**
     * 获取用户的点赞记录
     * @param userId 用户ID
     * @param targetType 目标类型，null表示所有类型
     * @return 点赞记录列表
     */
    List<LikeRecord> getUserLikeRecords(Long userId, Integer targetType);

    /**
     * 获取目标的点赞记录
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 点赞记录列表
     */
    List<LikeRecord> getTargetLikeRecords(Integer targetType, Long targetId);

    /**
     * 批量删除用户的点赞记录
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteUserLikeRecords(Long userId);

    /**
     * 批量删除目标的点赞记录
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 删除结果
     */
    boolean deleteTargetLikeRecords(Integer targetType, Long targetId);

    /**
     * 统计用户的点赞数量
     * @param userId 用户ID
     * @param targetType 目标类型，null表示所有类型
     * @return 点赞数量
     */
    Long countUserLikes(Long userId, Integer targetType);

    /**
     * 获取最近的点赞记录
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param limit 数量限制
     * @return 最近的点赞记录列表
     */
    List<LikeRecord> getRecentLikeRecords(Integer targetType, Long targetId, Integer limit);
}