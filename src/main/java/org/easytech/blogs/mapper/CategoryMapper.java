package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.easytech.blogs.entity.Category;

import java.util.List;

/**
 * 分类Mapper接口
 */
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询所有分类（按排序权重）
     */
    @Select("SELECT * FROM tb_category WHERE is_deleted = 0 ORDER BY sort_order ASC, create_time ASC")
    List<Category> selectAllCategories();

    /**
     * 根据名称查询分类
     */
    @Select("SELECT * FROM tb_category WHERE name = #{name} AND is_deleted = 0")
    Category findByName(@Param("name") String name);

    /**
     * 更新分类文章数量
     */
    @Update("UPDATE tb_category SET post_count = #{postCount} WHERE id = #{id}")
    int updatePostCount(@Param("id") Long id, @Param("postCount") Long postCount);

    /**
     * 获取有文章的分类
     */
    @Select("SELECT c.*, COUNT(p.id) as post_count " +
            "FROM tb_category c " +
            "LEFT JOIN tb_post p ON c.id = p.category_id AND p.status = 1 AND p.is_deleted = 0 " +
            "WHERE c.is_deleted = 0 " +
            "GROUP BY c.id " +
            "HAVING post_count > 0 " +
            "ORDER BY c.sort_order ASC")
    List<Category> selectCategoriesWithPosts();
}
