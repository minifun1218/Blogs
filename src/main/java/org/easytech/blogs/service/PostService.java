package org.easytech.blogs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.entity.Post;
import org.easytech.blogs.entity.Tag;

import java.util.List;

/**
 * 文章服务接口
 * 提供文章相关的业务逻辑处理
 */
public interface PostService {

    /**
     * 发布文章
     * @param post 文章信息
     * @param tagNames 标签名称列表
     * @return 发布结果
     */
    boolean publishPost(Post post, List<String> tagNames);

    /**
     * 发布文章（重载方法）
     * @param title 标题
     * @param summary 摘要
     * @param content 内容
     * @param authorId 作者ID
     * @param categoryId 分类ID
     * @param tagNames 标签名称列表
     * @return 发布的文章
     */
    Post publishPost(String title, String summary, String content, Long authorId, Long categoryId, List<String> tagNames);

    /**
     * 保存草稿
     * @param title 标题
     * @param content 内容
     * @param summary 摘要
     * @param authorId 作者ID
     * @param categoryId 分类ID
     * @param tagNames 标签名称列表
     * @return 保存结果
     */
    Post saveDraft(String title, String content, String summary, Long authorId, Long categoryId, List<String> tagNames);

    /**
     * 更新文章
     * @param post 文章信息
     * @param tagNames 标签名称列表
     * @return 更新结果
     */
    boolean updatePost(Post post, List<String> tagNames);

    /**
     * 更新文章（重载方法）
     * @param postId 文章ID
     * @param title 标题
     * @param summary 摘要
     * @param content 内容
     * @param categoryId 分类ID
     * @param tagNames 标签名称列表
     * @return 更新后的文章
     */
    Post updatePost(Long postId, String title, String summary, String content, Long categoryId, List<String> tagNames);

    /**
     * 删除文章（逻辑删除）
     * @param postId 文章ID
     * @param authorId 作者ID（权限验证）
     * @return 删除结果
     */
    boolean deletePost(Long postId, Long authorId);

    /**
     * 删除文章（重载方法）
     * @param postId 文章ID
     * @return 删除结果
     */
    default boolean deletePost(Long postId) {
        return deletePost(postId, null);
    }

    /**
     * 根据ID获取文章详情
     * @param postId 文章ID
     * @param incrementView 是否增加浏览量
     * @return 文章详情
     */
    Post getPostById(Long postId, boolean incrementView);

    /**
     * 获取文章详情（别名方法）
     * @param postId 文章ID
     * @return 文章详情
     */
    default Post getPostDetail(Long postId) {
        return getPostById(postId, true);
    }

    /**
     * 分页查询已发布的文章
     * @param page 分页参数
     * @return 文章分页列表
     */
    IPage<Post> getPublishedPostPage(Page<Post> page);

    /**
     * 根据分类分页查询文章
     * @param page 分页参数
     * @param categoryId 分类ID
     * @return 文章分页列表
     */
    IPage<Post> getPostsByCategory(Page<Post> page, Long categoryId);

    /**
     * 根据作者分页查询文章
     * @param page 分页参数
     * @param authorId 作者ID
     * @return 文章分页列表
     */
    IPage<Post> getPostsByAuthor(Page<Post> page, Long authorId);

    /**
     * 根据标签分页查询文章
     * @param page 分页参数
     * @param tagId 标签ID
     * @return 文章分页列表
     */
    IPage<Post> getPostsByTag(Page<Post> page, Long tagId);

    /**
     * 搜索文章
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 文章分页列表
     */
    IPage<Post> searchPosts(Page<Post> page, String keyword);

    /**
     * 获取热门文章
     * @param limit 数量限制
     * @return 热门文章列表
     */
    List<Post> getHotPosts(Integer limit);

    /**
     * 获取最新文章
     * @param limit 数量限制
     * @return 最新文章列表
     */
    List<Post> getLatestPosts(Integer limit);

    /**
     * 增加文章浏览量
     * @param postId 文章ID
     * @return 操作结果
     */
    boolean incrementViewCount(Long postId);

    /**
     * 点赞/取消点赞文章
     * @param postId 文章ID
     * @param userId 用户ID
     * @param isLike 是否点赞
     * @return 操作结果
     */
    boolean likePost(Long postId, Long userId, boolean isLike);

    /**
     * 点赞文章（重载方法）
     * @param postId 文章ID
     * @param userId 用户ID
     * @return 操作结果
     */
    default boolean likePost(Long postId, Long userId) {
        return likePost(postId, userId, true);
    }

    /**
     * 取消点赞文章（别名方法）
     * @param postId 文章ID
     * @param userId 用户ID
     * @return 操作结果
     */
    default boolean unlikePost(Long postId, Long userId) {
        return likePost(postId, userId, false);
    }

    /**
     * 检查用户是否已点赞文章
     * @param postId 文章ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean hasLiked(Long postId, Long userId);

    /**
     * 更新文章状态
     * @param postId 文章ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updatePostStatus(Long postId, Integer status);

    /**
     * 设置文章置顶
     * @param postId 文章ID
     * @param isTop 是否置顶
     * @return 操作结果
     */
    boolean setPostTop(Long postId, boolean isTop);

    /**
     * 设置文章置顶（别名方法）
     * @param postId 文章ID
     * @param isTop 是否置顶
     * @return 操作结果
     */
    default boolean topPost(Long postId, boolean isTop) {
        return setPostTop(postId, isTop);
    }

    /**
     * 获取文章标签
     * @param postId 文章ID
     * @return 标签列表
     */
    List<Tag> getPostTags(Long postId);

    /**
     * 统计文章数量
     * @param status 文章状态，null表示所有状态
     * @return 文章数量
     */
    Long countPosts(Integer status);

    /**
     * 获取推荐文章
     * @param postId 当前文章ID
     * @param limit 推荐数量
     * @return 推荐文章列表
     */
    List<Post> getRecommendPosts(Long postId, Integer limit);

    /**
     * 获取推荐文章（重载方法）
     * @param limit 推荐数量
     * @return 推荐文章列表
     */
    default List<Post> getRecommendPosts(Integer limit) {
        return getRecommendPosts(null, limit);
    }

    /**
     * 获取文章统计信息（浏览量、点赞数、评论数）
     * @param postId 文章ID
     * @return 文章信息
     */
    Post getPostStatistics(Long postId);

    /**
     * 检查用户是否为文章作者
     * @param postId 文章ID
     * @param username 用户名
     * @return 是否为作者
     */
    boolean isPostAuthor(Long postId, String username);

    /**
     * 检查用户是否为文章作者（通过用户ID）
     * @param postId 文章ID
     * @param userId 用户ID
     * @return 是否为作者
     */
    boolean isPostAuthorByUserId(Long postId, Long userId);
}