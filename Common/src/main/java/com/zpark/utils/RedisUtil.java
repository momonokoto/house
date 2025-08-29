package com.zpark.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  Redis 工具类
 * </p>
 *
 * @author fufuking

 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Long RELEASE_SUCCESS = 1L;
    private static final String RELEASE_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else " +
            "return 0 " +
            "end";

    public void setttl(String username,long timeout){
        redisTemplate.expire("auth:user::"+username, Duration.ofSeconds(timeout));
    }


    // 设置键值对
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 设置键值对并指定有效时间
    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
        return true;
    }

    //设置键值对以及前缀，并指定有效时间
    public boolean setWithPrefix(String prefix,String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(prefix+key, value, timeout, unit);
        return true;
    }

    // 设置键值对并指定有效时间
    public void set(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    // 获取值
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 获取值
    public String getString(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj == null ? null : obj.toString();
    }

    // 删除键
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    // 判断键是否存在
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // 如果不存在，则设置
    public Boolean setNx(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    // 如果不存在，则设置，附带过期时间
    public Boolean tryLock(String lockKey, String requestId, long seconds) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, seconds, TimeUnit.SECONDS);
    }

    // 如果不存在，则设置，附带过期时间
    public Boolean tryLock(String lockKey, String requestId, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, timeout, unit);
    }

    // 不存在返回true，存在则删除
    public Boolean releaseLock(String lockKey, String requestId){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(RELEASE_SCRIPT);
        redisScript.setResultType(Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        return RELEASE_SUCCESS.equals(result);
    }


    /**
     * 添加或更新最近联系人
     * @param userId 当前用户ID
     * @param contactId 联系人ID
     * @param timestamp 最后交互时间戳(秒)
     */
    public void addRecentContact(String userId, String contactId, long timestamp) {
        String key = "user:" + userId + ":recent_contacts";
        stringRedisTemplate.opsForZSet().add(key, contactId, timestamp);
    }

    /**
     * 获取最近联系人列表(按时间倒序)
     * @param userId 用户ID
     * @param count 获取数量
     * @return 联系人ID集合
     */
    public Set<String> getRecentContacts(String userId, long count) {
        String key = "user:" + userId + ":recent_contacts";
        return stringRedisTemplate.opsForZSet().reverseRange(key, 0, count - 1);
    }

    /**
     * 获取最近联系人列表带分数(时间戳)
     * @param userId 用户ID
     * @param count 获取数量
     * @return 联系人ID和分数(时间戳)的Entry集合
     */
    public Set<ZSetOperations.TypedTuple<String>> getRecentContactsWithScores(String userId, long count) {
        String key = "user:" + userId + ":recent_contacts";
        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, count - 1);
    }

    /**
     * 获取带时间戳的联系人列表(转换为Map)
     * @param userId 用户ID
     * @param count 获取数量
     * @return Map<联系人ID, 时间戳>
     */
    public Map<String, Double> getRecentContactsMap(String userId, long count) {
        Set<ZSetOperations.TypedTuple<String>> tuples = getRecentContactsWithScores(userId, count);
        Map<String, Double> result = new LinkedHashMap<>();
        if (tuples != null) {
            tuples.forEach(tuple -> result.put(tuple.getValue(), tuple.getScore()));
        }
        return result;
    }

    /**
     * 删除某个最近联系人
     * @param userId 用户ID
     * @param contactId 联系人ID
     * @return 是否删除成功
     */
    public boolean removeRecentContact(String userId, String contactId) {
        String key = "user:" + userId + ":recent_contacts";
        return stringRedisTemplate.opsForZSet().remove(key, contactId) > 0;
    }

    /**
     * 限制最近联系人列表大小(防止无限增长)
     * @param userId 用户ID
     * @param maxSize 最大保留数量
     */
    public void trimRecentContacts(String userId, long maxSize) {
        String key = "user:" + userId + ":recent_contacts";
        Long size = stringRedisTemplate.opsForZSet().zCard(key);
        if (size != null && size > maxSize) {
            stringRedisTemplate.opsForZSet().removeRange(key, 0, size - maxSize - 1);
        }
    }

    /**
     * 原子性操作：添加联系人并修剪列表
     * @param userId 用户ID
     * @param contactId 联系人ID
     * @param timestamp 时间戳
     * @param maxSize 最大保留数量
     */
    public void addAndTrimRecentContact(String userId, String contactId, long timestamp, long maxSize) {
        String luaScript = """
        local key = KEYS[1]
        local contact = ARGV[1]
        local timestamp = tonumber(ARGV[2])
        local max_size = tonumber(ARGV[3])
        
        -- 添加新记录
        redis.call('ZADD', key, timestamp, contact)
        
        -- 修剪列表
        local count = redis.call('ZCARD', key)
        if count > max_size then
            redis.call('ZREMRANGEBYRANK', key, 0, count-max_size-1)
        end
        return 1
        """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        String key = "user:" + userId + ":recent_contacts";
        stringRedisTemplate.execute(redisScript,
                Collections.singletonList(key),// KEYS列表
                contactId,// ARGV[1]
                String.valueOf(timestamp),// ARGV[2]
                String.valueOf(maxSize));// ARGV[3]
    }

    /**
     * 获取联系人的最后交互时间
     * @param userId 用户ID
     * @param contactId 联系人ID
     * @return 时间戳(秒)，不存在返回null
     */
    public Double getContactLastTime(String userId, String contactId) {
        String key = "user:" + userId + ":recent_contacts";
        return stringRedisTemplate.opsForZSet().score(key, contactId);
    }



}

