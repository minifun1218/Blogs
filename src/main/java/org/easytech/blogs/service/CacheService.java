package org.easytech.blogs.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * 缓存服务接口
 */
public interface CacheService {

    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 设置缓存并指定过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     */
    void set(String key, Object value, Duration timeout);

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    Object get(String key);

    /**
     * 获取缓存并指定类型
     * @param key 键
     * @param clazz 类型
     * @return 值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     * @param key 键
     * @return 是否删除成功
     */
    Boolean delete(String key);

    /**
     * 批量删除缓存
     * @param keys 键列表
     * @return 删除的数量
     */
    Long delete(List<String> keys);

    /**
     * 判断缓存是否存在
     * @param key 键
     * @return 是否存在
     */
    Boolean hasKey(String key);

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @return 是否设置成功
     */
    Boolean expire(String key, Duration timeout);

    /**
     * 获取过期时间
     * @param key 键
     * @return 过期时间（秒）
     */
    Long getExpire(String key);

    /**
     * 根据模式删除缓存
     * @param pattern 模式
     * @return 删除的数量
     */
    Long deleteByPattern(String pattern);

    /**
     * 获取所有匹配的键
     * @param pattern 模式
     * @return 键集合
     */
    Set<String> keys(String pattern);

    /**
     * 缓存列表
     * @param key 键
     * @param value 值
     * @return 列表长度
     */
    Long listRightPush(String key, Object value);

    /**
     * 获取列表
     * @param key 键
     * @param start 开始位置
     * @param end 结束位置
     * @return 列表
     */
    List<Object> listRange(String key, long start, long end);

    /**
     * 缓存集合
     * @param key 键
     * @param values 值
     * @return 添加的数量
     */
    Long setAdd(String key, Object... values);

    /**
     * 获取集合
     * @param key 键
     * @return 集合
     */
    Set<Object> setMembers(String key);

    /**
     * 哈希设置
     * @param key 键
     * @param hashKey 哈希键
     * @param value 值
     */
    void hashSet(String key, String hashKey, Object value);

    /**
     * 哈希获取
     * @param key 键
     * @param hashKey 哈希键
     * @return 值
     */
    Object hashGet(String key, String hashKey);

    /**
     * 递增
     * @param key 键
     * @param delta 增量
     * @return 递增后的值
     */
    Long increment(String key, long delta);

    /**
     * 递减
     * @param key 键
     * @param delta 减量
     * @return 递减后的值
     */
    Long decrement(String key, long delta);
}