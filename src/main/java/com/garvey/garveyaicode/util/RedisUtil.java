package com.garvey.garveyaicode.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用工具类（封装 RedisTemplate 核心操作）
 * 基于 Lettuce 实现，支持所有 Redis 核心数据类型操作
 */
@Component
@AllArgsConstructor
public class RedisUtil {

    /**
     * 注入 Spring 自动配置的 RedisTemplate（建议提前自定义序列化配置）
     */

    private final RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    // ===================== 基础通用操作 =====================

    /**
     * String类型：获取缓存值（指定返回类型，解决泛型丢失问题）
     * @param key 键
     * @param type 返回类型
     * @return 目标类型的对象
     */
    public <T> T get(String key, Class<T> type) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            Object value = operations.get(key);
            // 手动将 LinkedHashMap 转换为目标类型
            if (value == null) {
                return null;
            }
            // 利用 ObjectMapper 转换类型（需在 RedisUtil 中注入 ObjectMapper）
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间（秒）
     * @return 是否成功
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键（不能为 null）
     * @return 过期时间（秒），返回 -1 表示永久有效，-2 表示键不存在
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在，false 不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个或多个键
     */
    @SuppressWarnings("unchecked")
    public void del(String... keys) {
        if (keys != null && keys.length > 0) {
            redisTemplate.delete(Arrays.asList(keys));
        }
    }

    // ===================== String 类型操作 =====================

    /**
     * String类型：获取缓存值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * String类型：存入缓存（无过期时间）
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * String类型：存入缓存（带过期时间）
     *
     * @param key   键
     * @param value 值
     * @param time  时间（秒），time <= 0 时表示永久有效
     * @return 是否成功
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * String类型：递增（原子操作）
     *
     * @param key   键
     * @param delta 递增步长（>0）
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * String类型：递减（原子操作）
     *
     * @param key   键
     * @param delta 递减步长（>0）
     * @return 递减后的值
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ===================== Hash 类型操作 =====================

    /**
     * Hash类型：获取指定字段的值
     *
     * @param key     键
     * @param hashKey 字段
     * @return 值
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * Hash类型：获取指定键的所有字段和值
     *
     * @param key 键
     * @return 所有字段和值
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Hash类型：存入单个字段值
     *
     * @param key     键
     * @param hashKey 字段
     * @param value   值
     * @return 是否成功
     */
    public boolean hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hash类型：批量存入字段值
     *
     * @param key 键
     * @param map 多个字段的键值对
     * @return 是否成功
     */
    public boolean hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hash类型：删除指定字段
     *
     * @param key      键
     * @param hashKeys 可以传一个或多个字段
     */
    public void hDel(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * Hash类型：判断字段是否存在
     *
     * @param key     键
     * @param hashKey 字段
     * @return true 存在，false 不存在
     */
    public boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Hash类型：字段值递增
     *
     * @param key     键
     * @param hashKey 字段
     * @param delta   递增步长（>0）
     * @return 递增后的值
     */
    public double hIncr(String key, String hashKey, double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * Hash类型：字段值递减
     *
     * @param key     键
     * @param hashKey 字段
     * @param delta   递减步长（>0）
     * @return 递减后的值
     */
    public double hDecr(String key, String hashKey, double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }

    // ===================== List 类型操作 =====================

    /**
     * List类型：获取指定范围的元素
     *
     * @param key   键
     * @param start 开始索引（0 表示第一个元素）
     * @param end   结束索引（-1 表示最后一个元素）
     * @return 元素列表
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * List类型：获取列表长度
     *
     * @param key 键
     * @return 列表长度
     */
    public long lGetSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * List类型：获取指定索引的元素
     *
     * @param key   键
     * @param index 索引（正数：从左到右，负数：从右到左，如 -1 表示最后一个）
     * @return 元素
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * List类型：存入元素（从列表尾部插入）
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * List类型：批量存入元素（从列表尾部插入）
     *
     * @param key    键
     * @param values 元素列表
     * @return 是否成功
     */
    public boolean lSetAll(String key, List<Object> values) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * List类型：设置指定索引的元素值
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 是否成功
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * List类型：删除指定数量的指定元素
     *
     * @param key   键
     * @param count 删除数量（count > 0：删除前 count 个；count < 0：删除后 |count| 个；count = 0：删除所有）
     * @param value 要删除的元素
     * @return 删除的数量
     */
    public long lDel(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===================== Set 类型操作 =====================

    /**
     * Set类型：获取集合所有元素
     *
     * @param key 键
     * @return 元素集合
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set类型：判断元素是否在集合中
     *
     * @param key   键
     * @param value 元素
     * @return true 存在，false 不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set类型：存入元素
     *
     * @param key    键
     * @param values 可以传一个或多个元素
     * @return 存入的元素数量
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Set类型：删除元素
     *
     * @param key    键
     * @param values 可以传一个或多个元素
     * @return 删除的元素数量
     */
    public long sDel(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Set类型：获取集合大小
     *
     * @param key 键
     * @return 集合大小
     */
    public long sGetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===================== ZSet（有序集合）类型操作 =====================

    /**
     * ZSet类型：存入元素（带分数）
     *
     * @param key   键
     * @param value 元素
     * @param score 分数
     * @return 是否成功
     */
    public boolean zSet(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().add(key, value, score);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ZSet类型：删除元素
     *
     * @param key    键
     * @param values 可以传一个或多个元素
     * @return 删除的元素数量
     */
    public long zDel(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * ZSet类型：按分数递增排序，获取指定范围的元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 元素列表
     */
    public Set<Object> zGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ZSet类型：按分数递减排序，获取指定范围的元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 元素列表
     */
    public Set<Object> zGetReverse(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ZSet类型：获取元素的分数
     *
     * @param key   键
     * @param value 元素
     * @return 分数
     */
    public Double zGetScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * ZSet类型：元素分数递增
     *
     * @param key   键
     * @param value 元素
     * @param delta 递增步长
     * @return 递增后的分数
     */
    public Double zIncrScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * ZSet类型：获取集合大小
     *
     * @param key 键
     * @return 集合大小
     */
    public long zGetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    // ===================== 分布式锁 =====================

    /**
     * 获取分布式锁（SET NX EX 原子操作）
     *
     * @param lockKey   锁键
     * @param requestId 请求唯一标识（避免误删其他线程的锁）
     * @param expireTime 锁过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS));
    }

    /**
     * 释放分布式锁（Lua 脚本保证原子性）
     *
     * @param lockKey   锁键
     * @param requestId 请求唯一标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        // Lua 脚本：判断值相等才删除
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        return result != null && result > 0;
    }

    // ===================== 管道操作（批量执行提升性能） =====================

    /**
     * 执行管道操作
     *
     * @param callback 管道回调（自定义要执行的命令）
     * @return 执行结果列表
     */
    public List<Object> executePipeline(SessionCallback<List<Object>> callback) {
        return redisTemplate.executePipelined(callback);
    }
}
