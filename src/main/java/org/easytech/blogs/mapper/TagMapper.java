package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.easytech.blogs.entity.Tag;

import java.util.List;

/**
 * 标签Mapper接口
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 查询所有标签（按使用次数排序）
     */
    @Select("SELECT * FROM tb_tag WHERE is_deleted = 0 ORDER BY use_count DESC, create_time ASC")
    List<Tag> selectAllTags();

    /**
     * 根据名称查询标签
     */
    @Select("SELECT * FROM tb_tag WHERE name = #{name} AND is_deleted = 0")
    Tag findByName(@Param("name") String name);

    /**
     * 根据文章ID查询标签
     */
    @Select("SELECT t.* FROM tb_tag t " +
            "INNER JOIN tb_post_tag pt ON t.id = pt.tag_id " +
            "WHERE pt.post_id = #{postId} AND t.is_deleted = 0")
    List<Tag> selectTagsByPostId(@Param("postId") Long postId);

    /**
     * 更新标签使用次数
     */
    @Update("UPDATE tb_tag SET use_count = #{useCount} WHERE id = #{id}")
    int updateUseCount(@Param("id") Long id, @Param("useCount") Long useCount);

    /**
     * 获取热门标签
     */
    @Select("SELECT * FROM tb_tag WHERE is_deleted = 0 AND use_count > 0 ORDER BY use_count DESC LIMIT #{limit}")
    List<Tag> selectHotTags(@Param("limit") Integer limit);

    /**
     * 批量查询标签
     */
    @Select("<script>" +
            "SELECT * FROM tb_tag WHERE name IN " +
            "<foreach collection='names' item='name' open='(' separator=',' close=')'>" +
            "#{name}" +
            "</foreach>" +
            " AND is_deleted = 0" +
            "</script>")
    List<Tag> selectTagsByNames(@Param("names") List<String> names);
}
