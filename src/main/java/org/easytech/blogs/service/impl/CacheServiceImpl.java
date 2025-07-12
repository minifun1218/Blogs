package org.easytech.blogs.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.service.CacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 缓存服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis设置缓存失败，key: {}", key, e);
        }
    }

    @Override
    public void set(String key, Object value, Duration timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout);
        } catch (Exception e) {
            log.error("Redis设置缓存失败，key: {}, timeout: {}", key, timeout, e);
        }
    }

    @Override
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis获取缓存失败，key: {}", key, e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value == null ? null : (T) value;
        } catch (Exception e) {
            log.error("Redis获取缓存失败，key: {}, class: {}", key, clazz.getName(), e);
            return null;
        }
    }

    @Override
    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis删除缓存失败，key: {}", key, e);
            return false;
        }
    }

    @Override
    public Long delete(List<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Redis批量删除缓存失败，keys: {}", keys, e);
            return 0L;
        }
    }

    @Override
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis检查键是否存在失败，key: {}", key, e);
            return false;
        }
    }

    @Override
    public Boolean expire(String key, Duration timeout) {
        try {
            return redisTemplate.expire(key, timeout);
        } catch (Exception e) {
            log.error("Redis设置过期时间失败，key: {}, timeout: {}", key, timeout, e);
            return false;
        }
    }

    @Override
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            log.error("Redis获取过期时间失败，key: {}", key, e);
            return -1L;
        }
    }

    @Override
    public Long deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                return redisTemplate.delete(keys);
            }
            return 0L;
        } catch (Exception e) {
            log.error("Redis按模式删除缓存失败，pattern: {}", pattern, e);
            return 0L;
        }
    }

    @Override
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Redis获取匹配键失败，pattern: {}", pattern, e);
            return Set.of();
        }
    }

    @Override
    public Long listRightPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("Redis列表右推失败，key: {}", key, e);
            return 0L;
        }
    }

    @Override
    public List<Object> listRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis获取列表范围失败，key: {}, start: {}, end: {}", key, start, end, e);
            return List.of();
        }
    }

    @Override
    public Long setAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Redis集合添加失败，key: {}", key, e);
            return 0L;
        }
    }

    @Override
    public Set<Object> setMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis获取集合成员失败，key: {}", key, e);
            return Set.of();
        }
    }

    @Override
    public void hashSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("Redis哈希设置失败，key: {}, hashKey: {}", key, hashKey, e);
        }
    }

    @Override
    public Object hashGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("Redis哈希获取失败，key: {}, hashKey: {}", key, hashKey, e);
            return null;
        }
    }

    @Override
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis递增失败，key: {}, delta: {}", key, delta, e);
            return 0L;
        }
    }

    @Override
    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("Redis递减失败，key: {}, delta: {}", key, delta, e);
            return 0L;
        }
    }
}