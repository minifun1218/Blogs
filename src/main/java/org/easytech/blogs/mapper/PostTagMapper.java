package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.easytech.blogs.entity.PostTag;

import java.util.List;

/**
 * 文章标签关联Mapper接口
 */
@Mapper
public interface PostTagMapper extends BaseMapper<PostTag> {

    /**
     * 根据文章ID删除标签关联
     */
    @Delete("DELETE FROM tb_post_tag WHERE post_id = #{postId}")
    int deleteByPostId(@Param("postId") Long postId);

    /**
     * 根据标签ID删除关联
     */
    @Delete("DELETE FROM tb_post_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(@Param("tagId") Long tagId);

    /**
     * 根据文章ID查询标签ID列表
     */
    @Select("SELECT tag_id FROM tb_post_tag WHERE post_id = #{postId}")
    List<Long> selectTagIdsByPostId(@Param("postId") Long postId);

    /**
     * 根据标签ID查询文章ID列表
     */
    @Select("SELECT post_id FROM tb_post_tag WHERE tag_id = #{tagId}")
    List<Long> selectPostIdsByTagId(@Param("tagId") Long tagId);

    /**
     * 统计标签使用次数
     */
    @Select("SELECT COUNT(*) FROM tb_post_tag WHERE tag_id = #{tagId}")
    Long countByTagId(@Param("tagId") Long tagId);
}
