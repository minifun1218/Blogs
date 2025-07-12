package org.easytech.blogs.service;

import org.easytech.blogs.entity.PostTag;

import java.util.List;

/**
 * 文章标签关联服务接口
 * 提供文章标签关联相关的业务逻辑处理
 */
public interface PostTagService {

    /**
     * 为文章添加标签
     * @param postId 文章ID
     * @param tagId 标签ID
     * @return 添加结果
     */
    boolean addPostTag(Long postId, Long tagId);

    /**
     * 为文章批量添加标签
     * @param postId 文章ID
     * @param tagIds 标签ID列表
     * @return 添加结果
     */
    boolean addPostTags(Long postId, List<Long> tagIds);

    /**
     * 为文章设置标签（先删除原有关联，再添加新关联）
     * @param postId 文章ID
     * @param tagIds 标签ID列表
     * @return 设置结果
     */
    boolean setPostTags(Long postId, List<Long> tagIds);

    /**
     * 删除文章标签关联
     * @param postId 文章ID
     * @param tagId 标签ID
     * @return 删除结果
     */
    boolean removePostTag(Long postId, Long tagId);

    /**
     * 删除文章的所有标签关联
     * @param postId 文章ID
     * @return 删除结果
     */
    boolean removeAllPostTags(Long postId);

    /**
     * 删除标签的所有文章关联
     * @param tagId 标签ID
     * @return 删除结果
     */
    boolean removeAllTagPosts(Long tagId);

    /**
     * 根据文章ID获取标签ID列表
     * @param postId 文章ID
     * @return 标签ID列表
     */
    List<Long> getTagIdsByPostId(Long postId);

    /**
     * 根据标签ID获取文章ID列表
     * @param tagId 标签ID
     * @return 文章ID列表
     */
    List<Long> getPostIdsByTagId(Long tagId);

    /**
     * 统计标签的文章数量
     * @param tagId 标签ID
     * @return 文章数量
     */
    Long countPostsByTagId(Long tagId);

    /**
     * 统计文章的标签数量
     * @param postId 文章ID
     * @return 标签数量
     */
    Long countTagsByPostId(Long postId);

    /**
     * 检查文章是否关联了指定标签
     * @param postId 文章ID
     * @param tagId 标签ID
     * @return 是否关联
     */
    boolean hasPostTag(Long postId, Long tagId);

    /**
     * 获取所有文章标签关联记录
     * @return 关联记录列表
     */
    List<PostTag> getAllPostTags();

    /**
     * 根据文章ID列表批量删除文章标签关联
     * @param postIds 文章ID列表
     * @return 删除结果
     */
    boolean batchRemovePostTags(List<Long> postIds);

    /**
     * 根据标签ID列表批量删除文章标签关联
     * @param tagIds 标签ID列表
     * @return 删除结果
     */
    boolean batchRemoveTagPosts(List<Long> tagIds);

    /**
     * 清理无效的关联记录（文章或标签已被删除）
     * @return 清理的记录数量
     */
    int cleanInvalidRelations();

    /**
     * 同步更新标签使用次数
     * @param tagId 标签ID
     * @return 更新结果
     */
    boolean updateTagUseCount(Long tagId);

    /**
     * 批量同步更新所有标签的使用次数
     * @return 更新结果
     */
    boolean syncAllTagUseCounts();
}