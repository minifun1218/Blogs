package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.easytech.blogs.entity.LikeRecord;

import java.util.List;

/**
 * 点赞记录Mapper接口
 */
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    /**
     * 统计目标的点赞数
     */
    @Select("SELECT COUNT(*) FROM tb_like_record WHERE target_type = #{targetType} AND target_id = #{targetId} AND status = 1")
    Long countLikes(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 获取用户点赞的目标列表
     */
    @Select("SELECT target_id FROM tb_like_record WHERE user_id = #{userId} AND target_type = #{targetType} AND status = 1")
    List<Long> selectUserLikedTargets(@Param("userId") Long userId, @Param("targetType") Integer targetType);

    /**
     * 检查用户是否已点赞
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_like_record WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId} AND status = 1")
    boolean hasLiked(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);
}
