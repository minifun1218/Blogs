package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.easytech.blogs.entity.Post;

import java.util.List;

/**
 * 文章Mapper接口
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 分页查询已发布的文章
     */
    @Select("SELECT p.*, u.username as author_name, u.nickname as author_nickname, c.name as category_name " +
            "FROM tb_post p " +
            "LEFT JOIN tb_user u ON p.author_id = u.id " +
            "LEFT JOIN tb_category c ON p.category_id = c.id " +
            "WHERE p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY p.is_top DESC, p.create_time DESC")
    IPage<Post> selectPublishedPostPage(Page<Post> page);

    /**
     * 根据分类查询文章
     */
    @Select("SELECT p.*, u.username as author_name, c.name as category_name " +
            "FROM tb_post p " +
            "LEFT JOIN tb_user u ON p.author_id = u.id " +
            "LEFT JOIN tb_category c ON p.category_id = c.id " +
            "WHERE p.category_id = #{categoryId} AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    IPage<Post> selectPostsByCategory(Page<Post> page, @Param("categoryId") Long categoryId);

    /**
     * 根据作者查询文章
     */
    @Select("SELECT * FROM tb_post WHERE author_id = #{authorId} AND is_deleted = 0 ORDER BY create_time DESC")
    IPage<Post> selectPostsByAuthor(Page<Post> page, @Param("authorId") Long authorId);

    /**
     * 搜索文章
     */
    @Select("SELECT p.*, u.username as author_name, c.name as category_name " +
            "FROM tb_post p " +
            "LEFT JOIN tb_user u ON p.author_id = u.id " +
            "LEFT JOIN tb_category c ON p.category_id = c.id " +
            "WHERE (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    IPage<Post> searchPosts(Page<Post> page, @Param("keyword") String keyword);

    /**
     * 获取热门文章
     */
    @Select("SELECT p.*, u.username as author_name, c.name as category_name " +
            "FROM tb_post p " +
            "LEFT JOIN tb_user u ON p.author_id = u.id " +
            "LEFT JOIN tb_category c ON p.category_id = c.id " +
            "WHERE p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY p.view_count DESC, p.like_count DESC " +
            "LIMIT #{limit}")
    List<Post> selectHotPosts(@Param("limit") Integer limit);

    /**
     * 增加浏览量
     */
    @Update("UPDATE tb_post SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 更新点赞数
     */
    @Update("UPDATE tb_post SET like_count = #{likeCount} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("likeCount") Long likeCount);

    /**
     * 更新评论数
     */
    @Update("UPDATE tb_post SET comment_count = #{commentCount} WHERE id = #{id}")
    int updateCommentCount(@Param("id") Long id, @Param("commentCount") Long commentCount);

    /**
     * 根据标签查询文章
     */
    @Select("SELECT DISTINCT p.*, u.username as author_name, c.name as category_name " +
            "FROM tb_post p " +
            "LEFT JOIN tb_user u ON p.author_id = u.id " +
            "LEFT JOIN tb_category c ON p.category_id = c.id " +
            "INNER JOIN tb_post_tag pt ON p.id = pt.post_id " +
            "WHERE pt.tag_id = #{tagId} AND p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    IPage<Post> selectPostsByTag(Page<Post> page, @Param("tagId") Long tagId);

    /**
     * 统计文章数量
     */
    @Select("SELECT COUNT(*) FROM tb_post WHERE status = #{status} AND is_deleted = 0")
    Long countByStatus(@Param("status") Integer status);

    /**
     * 获取最新文章
     */
    @Select("SELECT p.*, u.username as author_name, u.nickname as author_nickname, c.name as category_name " +
            "FROM tb_post p " +
            "LEFT JOIN tb_user u ON p.author_id = u.id " +
            "LEFT JOIN tb_category c ON p.category_id = c.id " +
            "WHERE p.status = 1 AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC " +
            "LIMIT #{limit}")
    List<Post> selectLatestPosts(@Param("limit") Integer limit);
}
