package org.easytech.blogs.service;

import org.easytech.blogs.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 * 提供标签相关的业务逻辑处理
 */
public interface TagService {

    /**
     * 创建标签
     * @param tag 标签信息
     * @return 创建结果
     */
    boolean createTag(Tag tag);

    /**
     * 创建标签（重载方法）
     * @param name 标签名称
     * @param color 标签颜色
     * @return 创建的标签对象
     */
    Tag createTag(String name, String color);

    /**
     * 更新标签
     * @param tag 标签信息
     * @return 更新结果
     */
    boolean updateTag(Tag tag);

    /**
     * 更新标签（重载方法）
     * @param id 标签ID
     * @param name 标签名称
     * @param color 标签颜色
     * @return 更新的标签对象
     */
    Tag updateTag(Long id, String name, String color);

    /**
     * 删除标签
     * @param tagId 标签ID
     * @return 删除结果
     */
    boolean deleteTag(Long tagId);

    /**
     * 根据ID获取标签
     * @param tagId 标签ID
     * @return 标签信息
     */
    Tag getTagById(Long tagId);

    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<Tag> getAllTags();

    /**
     * 根据名称查询标签
     * @param name 标签名称
     * @return 标签信息
     */
    Tag getTagByName(String name);

    /**
     * 根据文章ID获取标签列表
     * @param postId 文章ID
     * @return 标签列表
     */
    List<Tag> getTagsByPostId(Long postId);

    /**
     * 获取热门标签
     * @param limit 数量限制
     * @return 热门标签列表
     */
    List<Tag> getHotTags(Integer limit);

    /**
     * 批量获取或创建标签
     * @param tagNames 标签名称列表
     * @return 标签列表
     */
    List<Tag> getOrCreateTags(List<String> tagNames);

    /**
     * 检查标签名称是否存在
     * @param name 标签名称
     * @param excludeId 排除的标签ID
     * @return 是否存在
     */
    boolean isTagNameExists(String name, Long excludeId);

    /**
     * 更新标签使用次数
     * @param tagId 标签ID
     * @return 更新结果
     */
    boolean updateTagUseCount(Long tagId);

    /**
     * 批量更新所有标签的使用次数
     * @return 更新结果
     */
    boolean updateAllTagUseCounts();

    /**
     * 清理未使用的标签
     * @return 清理数量
     */
    int cleanUnusedTags();

    /**
     * 根据名称查询标签（别名方法）
     * @param name 标签名称
     * @return 标签信息
     */
    default Tag findByName(String name) {
        return getTagByName(name);
    }

    /**
     * 根据ID获取标签（别名方法）
     * @param tagId 标签ID
     * @return 标签信息
     */
    default Tag getById(Long tagId) {
        return getTagById(tagId);
    }

    /**
     * 检查标签名称是否存在（别名方法）
     * @param name 标签名称
     * @return 是否存在
     */
    default boolean existsByName(String name) {
        return isTagNameExists(name, null);
    }

    /**
     * 根据标签名称列表批量获取标签（别名方法）
     * @param tagNames 标签名称列表
     * @return 标签列表
     */
    default List<Tag> getTagsByNames(List<String> tagNames) {
        return getOrCreateTags(tagNames);
    }

    /**
     * 更新标签使用次数（别名方法）
     * @param tagId 标签ID
     * @return 更新结果
     */
    default boolean updateUseCount(Long tagId) {
        return updateTagUseCount(tagId);
    }
}