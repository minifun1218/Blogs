package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.easytech.blogs.entity.Comment;

import java.util.List;

/**
 * 评论Mapper接口
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 根据文章ID查询评论（树形结构）
     */
    @Select("SELECT c.*, u.username, u.nickname, u.avatar " +
            "FROM tb_comment c " +
            "LEFT JOIN tb_user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} AND c.status = 1 AND c.is_deleted = 0 " +
            "ORDER BY c.parent_id ASC, c.create_time ASC")
    List<Comment> selectCommentsByPostId(@Param("postId") Long postId);

    /**
     * 分页查询评论
     */
    @Select("SELECT c.*, u.username, u.nickname, u.avatar, p.title as post_title " +
            "FROM tb_comment c " +
            "LEFT JOIN tb_user u ON c.user_id = u.id " +
            "LEFT JOIN tb_post p ON c.post_id = p.id " +
            "WHERE c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    IPage<Comment> selectCommentPage(Page<Comment> page);

    /**
     * 根据用户查询评论
     */
    @Select("SELECT c.*, p.title as post_title " +
            "FROM tb_comment c " +
            "LEFT JOIN tb_post p ON c.post_id = p.id " +
            "WHERE c.user_id = #{userId} AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    IPage<Comment> selectCommentsByUserId(Page<Comment> page, @Param("userId") Long userId);

    /**
     * 根据状态查询评论
     */
    @Select("SELECT c.*, u.username, u.nickname, p.title as post_title " +
            "FROM tb_comment c " +
            "LEFT JOIN tb_user u ON c.user_id = u.id " +
            "LEFT JOIN tb_post p ON c.post_id = p.id " +
            "WHERE c.status = #{status} AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    IPage<Comment> selectCommentsByStatus(Page<Comment> page, @Param("status") Integer status);

    /**
     * 获取子评论
     */
    @Select("SELECT c.*, u.username, u.nickname, u.avatar " +
            "FROM tb_comment c " +
            "LEFT JOIN tb_user u ON c.user_id = u.id " +
            "WHERE c.parent_id = #{parentId} AND c.status = 1 AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC")
    List<Comment> selectChildComments(@Param("parentId") Long parentId);

    /**
     * 统计文章评论数
     */
    @Select("SELECT COUNT(*) FROM tb_comment WHERE post_id = #{postId} AND status = 1 AND is_deleted = 0")
    Long countByPostId(@Param("postId") Long postId);

    /**
     * 统计用户评论数
     */
    @Select("SELECT COUNT(*) FROM tb_comment WHERE user_id = #{userId} AND is_deleted = 0")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 更新点赞数
     */
    @Update("UPDATE tb_comment SET like_count = #{likeCount} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("likeCount") Long likeCount);

    /**
     * 批量更新评论状态
     */
    @Update("<script>" +
            "UPDATE tb_comment SET status = #{status} WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
