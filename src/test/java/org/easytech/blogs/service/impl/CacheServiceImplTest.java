package org.easytech.blogs.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.HashOperations;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 缓存服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class CacheServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private CacheServiceImpl cacheService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void testSet() {
        // Given
        String key = "test:key";
        String value = "test value";

        // When
        cacheService.set(key, value);

        // Then
        verify(valueOperations).set(key, value);
    }

    @Test
    void testSetWithTimeout() {
        // Given
        String key = "test:key";
        String value = "test value";
        Duration timeout = Duration.ofMinutes(30);

        // When
        cacheService.set(key, value, timeout);

        // Then
        verify(valueOperations).set(key, value, timeout);
    }

    @Test
    void testGet() {
        // Given
        String key = "test:key";
        String expectedValue = "test value";
        when(valueOperations.get(key)).thenReturn(expectedValue);

        // When
        Object result = cacheService.get(key);

        // Then
        assertEquals(expectedValue, result);
        verify(valueOperations).get(key);
    }

    @Test
    void testGetWithClass() {
        // Given
        String key = "test:key";
        String expectedValue = "test value";
        when(valueOperations.get(key)).thenReturn(expectedValue);

        // When
        String result = cacheService.get(key, String.class);

        // Then
        assertEquals(expectedValue, result);
        verify(valueOperations).get(key);
    }

    @Test
    void testGetWithClassReturnsNull() {
        // Given
        String key = "test:key";
        when(valueOperations.get(key)).thenReturn(null);

        // When
        String result = cacheService.get(key, String.class);

        // Then
        assertNull(result);
        verify(valueOperations).get(key);
    }

    @Test
    void testDelete() {
        // Given
        String key = "test:key";
        when(redisTemplate.delete(key)).thenReturn(true);

        // When
        Boolean result = cacheService.delete(key);

        // Then
        assertTrue(result);
        verify(redisTemplate).delete(key);
    }

    @Test
    void testDeleteMultiple() {
        // Given
        List<String> keys = List.of("key1", "key2", "key3");
        when(redisTemplate.delete(keys)).thenReturn(3L);

        // When
        Long result = cacheService.delete(keys);

        // Then
        assertEquals(3L, result);
        verify(redisTemplate).delete(keys);
    }

    @Test
    void testHasKey() {
        // Given
        String key = "test:key";
        when(redisTemplate.hasKey(key)).thenReturn(true);

        // When
        Boolean result = cacheService.hasKey(key);

        // Then
        assertTrue(result);
        verify(redisTemplate).hasKey(key);
    }

    @Test
    void testExpire() {
        // Given
        String key = "test:key";
        Duration timeout = Duration.ofHours(1);
        when(redisTemplate.expire(key, timeout)).thenReturn(true);

        // When
        Boolean result = cacheService.expire(key, timeout);

        // Then
        assertTrue(result);
        verify(redisTemplate).expire(key, timeout);
    }

    @Test
    void testGetExpire() {
        // Given
        String key = "test:key";
        when(redisTemplate.getExpire(key)).thenReturn(3600L);

        // When
        Long result = cacheService.getExpire(key);

        // Then
        assertEquals(3600L, result);
        verify(redisTemplate).getExpire(key);
    }

    @Test
    void testDeleteByPattern() {
        // Given
        String pattern = "test:*";
        Set<String> matchingKeys = Set.of("test:key1", "test:key2");
        when(redisTemplate.keys(pattern)).thenReturn(matchingKeys);
        when(redisTemplate.delete(matchingKeys)).thenReturn(2L);

        // When
        Long result = cacheService.deleteByPattern(pattern);

        // Then
        assertEquals(2L, result);
        verify(redisTemplate).keys(pattern);
        verify(redisTemplate).delete(matchingKeys);
    }

    @Test
    void testDeleteByPatternNoMatches() {
        // Given
        String pattern = "test:*";
        when(redisTemplate.keys(pattern)).thenReturn(Set.of());

        // When
        Long result = cacheService.deleteByPattern(pattern);

        // Then
        assertEquals(0L, result);
        verify(redisTemplate).keys(pattern);
        verify(redisTemplate, never()).delete(any(Set.class));
    }

    @Test
    void testKeys() {
        // Given
        String pattern = "test:*";
        Set<String> expectedKeys = Set.of("test:key1", "test:key2");
        when(redisTemplate.keys(pattern)).thenReturn(expectedKeys);

        // When
        Set<String> result = cacheService.keys(pattern);

        // Then
        assertEquals(expectedKeys, result);
        verify(redisTemplate).keys(pattern);
    }

    @Test
    void testListRightPush() {
        // Given
        String key = "test:list";
        String value = "item1";
        when(listOperations.rightPush(key, value)).thenReturn(1L);

        // When
        Long result = cacheService.listRightPush(key, value);

        // Then
        assertEquals(1L, result);
        verify(listOperations).rightPush(key, value);
    }

    @Test
    void testListRange() {
        // Given
        String key = "test:list";
        List<Object> expectedList = List.of("item1", "item2");
        when(listOperations.range(key, 0, -1)).thenReturn(expectedList);

        // When
        List<Object> result = cacheService.listRange(key, 0, -1);

        // Then
        assertEquals(expectedList, result);
        verify(listOperations).range(key, 0, -1);
    }

    @Test
    void testSetAdd() {
        // Given
        String key = "test:set";
        Object[] values = {"value1", "value2"};
        when(setOperations.add(key, values)).thenReturn(2L);

        // When
        Long result = cacheService.setAdd(key, values);

        // Then
        assertEquals(2L, result);
        verify(setOperations).add(key, values);
    }

    @Test
    void testSetMembers() {
        // Given
        String key = "test:set";
        Set<Object> expectedSet = Set.of("value1", "value2");
        when(setOperations.members(key)).thenReturn(expectedSet);

        // When
        Set<Object> result = cacheService.setMembers(key);

        // Then
        assertEquals(expectedSet, result);
        verify(setOperations).members(key);
    }

    @Test
    void testHashSet() {
        // Given
        String key = "test:hash";
        String hashKey = "field1";
        String value = "value1";

        // When
        cacheService.hashSet(key, hashKey, value);

        // Then
        verify(hashOperations).put(key, hashKey, value);
    }

    @Test
    void testHashGet() {
        // Given
        String key = "test:hash";
        String hashKey = "field1";
        String expectedValue = "value1";
        when(hashOperations.get(key, hashKey)).thenReturn(expectedValue);

        // When
        Object result = cacheService.hashGet(key, hashKey);

        // Then
        assertEquals(expectedValue, result);
        verify(hashOperations).get(key, hashKey);
    }

    @Test
    void testIncrement() {
        // Given
        String key = "test:counter";
        long delta = 5L;
        when(valueOperations.increment(key, delta)).thenReturn(10L);

        // When
        Long result = cacheService.increment(key, delta);

        // Then
        assertEquals(10L, result);
        verify(valueOperations).increment(key, delta);
    }

    @Test
    void testDecrement() {
        // Given
        String key = "test:counter";
        long delta = 3L;
        when(valueOperations.decrement(key, delta)).thenReturn(2L);

        // When
        Long result = cacheService.decrement(key, delta);

        // Then
        assertEquals(2L, result);
        verify(valueOperations).decrement(key, delta);
    }

    @Test
    void testSetWithException() {
        // Given
        String key = "test:key";
        String value = "test value";
        doThrow(new RuntimeException("Redis connection failed"))
                .when(valueOperations).set(key, value);

        // When & Then
        assertDoesNotThrow(() -> cacheService.set(key, value));
        verify(valueOperations).set(key, value);
    }

    @Test
    void testGetWithException() {
        // Given
        String key = "test:key";
        when(valueOperations.get(key)).thenThrow(new RuntimeException("Redis connection failed"));

        // When
        Object result = cacheService.get(key);

        // Then
        assertNull(result);
        verify(valueOperations).get(key);
    }
}