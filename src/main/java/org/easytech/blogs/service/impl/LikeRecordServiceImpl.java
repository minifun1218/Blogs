package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.LikeRecord;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.LikeRecordMapper;
import org.easytech.blogs.service.LikeRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 点赞记录服务实现类
 * 实现点赞记录相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeRecordServiceImpl implements LikeRecordService {

    private final LikeRecordMapper likeRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean like(Long userId, Integer targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) {
            throw new ValidationException("参数不能为空");
        }

        try {
            // 查找是否已存在点赞记录
            LikeRecord existRecord = likeRecordMapper.selectOne(
                new QueryWrapper<LikeRecord>()
                    .eq("user_id", userId)
                    .eq("target_type", targetType)
                    .eq("target_id", targetId)
            );

            if (existRecord == null) {
                // 创建新的点赞记录
                LikeRecord likeRecord = new LikeRecord();
                likeRecord.setUserId(userId);
                likeRecord.setTargetType(targetType);
                likeRecord.setTargetId(targetId);
                likeRecord.setStatus(1); // 1-点赞，0-取消点赞

                int result = likeRecordMapper.insert(likeRecord);
                if (result > 0) {
                    log.info("点赞成功，用户ID: {}, 目标类型: {}, 目标ID: {}", userId, targetType, targetId);
                    return true;
                }
            } else if (existRecord.getStatus() != 1) {
                // 重新点赞
                existRecord.setStatus(1);
                int result = likeRecordMapper.updateById(existRecord);
                if (result > 0) {
                    log.info("重新点赞成功，用户ID: {}, 目标类型: {}, 目标ID: {}", userId, targetType, targetId);
                    return true;
                }
            } else {
                // 已经点赞过了
                return true;
            }
        } catch (Exception e) {
            log.error("点赞操作失败，用户ID: {}, 目标类型: {}, 目标ID: {}", userId, targetType, targetId, e);
            throw new BusinessException("点赞操作失败");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlike(Long userId, Integer targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) {
            throw new ValidationException("参数不能为空");
        }

        try {
            // 查找点赞记录
            LikeRecord existRecord = likeRecordMapper.selectOne(
                new QueryWrapper<LikeRecord>()
                    .eq("user_id", userId)
                    .eq("target_type", targetType)
                    .eq("target_id", targetId)
            );

            if (existRecord != null && existRecord.getStatus() == 1) {
                // 取消点赞
                existRecord.setStatus(0);
                int result = likeRecordMapper.updateById(existRecord);
                if (result > 0) {
                    log.info("取消点赞成功，用户ID: {}, 目标类型: {}, 目标ID: {}", userId, targetType, targetId);
                    return true;
                }
            } else {
                // 没有点赞记录或已经取消
                return true;
            }
        } catch (Exception e) {
            log.error("取消点赞操作失败，用户ID: {}, 目标类型: {}, 目标ID: {}", userId, targetType, targetId, e);
            throw new BusinessException("取消点赞操作失败");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long userId, Integer targetType, Long targetId) {
        if (hasLiked(userId, targetType, targetId)) {
            unlike(userId, targetType, targetId);
            return false; // 返回当前状态：未点赞
        } else {
            like(userId, targetType, targetId);
            return true; // 返回当前状态：已点赞
        }
    }

    @Override
    public boolean hasLiked(Long userId, Integer targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) {
            return false;
        }
        return likeRecordMapper.hasLiked(userId, targetType, targetId);
    }

    @Override
    public Long countLikes(Integer targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            return 0L;
        }
        return likeRecordMapper.countLikes(targetType, targetId);
    }

    @Override
    public List<Long> getUserLikedTargets(Long userId, Integer targetType) {
        if (userId == null || targetType == null) {
            return List.of();
        }
        return likeRecordMapper.selectUserLikedTargets(userId, targetType);
    }

    @Override
    public LikeRecord getLikeRecordById(Long recordId) {
        if (recordId == null) {
            return null;
        }
        return likeRecordMapper.selectById(recordId);
    }

    @Override
    public List<LikeRecord> getUserLikeRecords(Long userId, Integer targetType) {
        if (userId == null) {
            return List.of();
        }

        QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("status", 1); // 只查询有效的点赞记录

        if (targetType != null) {
            queryWrapper.eq("target_type", targetType);
        }

        queryWrapper.orderByDesc("create_time");

        return likeRecordMapper.selectList(queryWrapper);
    }

    @Override
    public List<LikeRecord> getTargetLikeRecords(Integer targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            return List.of();
        }

        QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target_type", targetType)
                   .eq("target_id", targetId)
                   .eq("status", 1) // 只查询有效的点赞记录
                   .orderByDesc("create_time");

        return likeRecordMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserLikeRecords(Long userId) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        try {
            QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);

            int result = likeRecordMapper.delete(queryWrapper);
            log.info("删除用户点赞记录成功，用户ID: {}, 删除数量: {}", userId, result);
            return true;
        } catch (Exception e) {
            log.error("删除用户点赞记录失败，用户ID: {}", userId, e);
            throw new BusinessException("删除用户点赞记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTargetLikeRecords(Integer targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            throw new ValidationException("目标类型和目标ID不能为空");
        }

        try {
            QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("target_type", targetType)
                       .eq("target_id", targetId);

            int result = likeRecordMapper.delete(queryWrapper);
            log.info("删除目标点赞记录成功，目标类型: {}, 目标ID: {}, 删除数量: {}", targetType, targetId, result);
            return true;
        } catch (Exception e) {
            log.error("删除目标点赞记录失败，目标类型: {}, 目标ID: {}", targetType, targetId, e);
            throw new BusinessException("删除目标点赞记录失败");
        }
    }

    @Override
    public Long countUserLikes(Long userId, Integer targetType) {
        if (userId == null) {
            return 0L;
        }

        QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("status", 1); // 只统计有效的点赞记录

        if (targetType != null) {
            queryWrapper.eq("target_type", targetType);
        }

        return likeRecordMapper.selectCount(queryWrapper);
    }

    @Override
    public List<LikeRecord> getRecentLikeRecords(Integer targetType, Long targetId, Integer limit) {
        if (targetType == null || targetId == null) {
            return List.of();
        }

        if (limit == null || limit <= 0) {
            limit = 10;
        }

        QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target_type", targetType)
                   .eq("target_id", targetId)
                   .eq("status", 1) // 只查询有效的点赞记录
                   .orderByDesc("create_time")
                   .last("LIMIT " + limit);

        return likeRecordMapper.selectList(queryWrapper);
    }
}