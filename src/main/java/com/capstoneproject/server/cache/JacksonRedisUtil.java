package com.capstoneproject.server.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */

public class JacksonRedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(JacksonRedisUtil.class);
    public static final Duration DEFAULT_DURATION = Duration.ofMinutes(30);
    private final ConcurrentMap<Class<?>, RedisTemplate<String, Object>> templates = new ConcurrentHashMap<>();
    private final RedisConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;

    public JacksonRedisUtil(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        this.connectionFactory = redisConnectionFactory;
        this.objectMapper = objectMapper;
    }


    public JacksonRedisUtil(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .registerModule(new JavaTimeModule());
    }

    public <T> void put(String key, T data, Duration expiration){
        if (data == null) {
            logger.warn("No data was given. Cancelling cache for key [{}]", key);
            return;
        }

        RedisTemplate<String, Object> template = getRedisTemplate(data.getClass());

        template.opsForValue().set(key, data, expiration);
        logger.debug("Inserted/Updated cache entry for key [{}]", key);
    }

    public <T> void put(String key, List<T> items, Duration expiration) {
        if (CollectionUtils.isEmpty(items)) {
            logger.warn("No data was given. Cancelling cache for key [{}]", key);
            return;
        }

        RedisTemplate<String, Object> template = getRedisTemplate(List.class);

        try {
            template.opsForValue().set(key, objectMapper.writeValueAsString(items), expiration);
            logger.debug("Inserted/Updated cache entry for key [{}]", key);
        } catch (JsonProcessingException e) {
//            logger.error("Failed to cache list {} items for key [{}]. {}", items.get(0).getClass().getSimpleName(), key, e.getMessage());
        }
    }

    public <T> T getAsMono(String key, Class<T> clazz) {
        RedisTemplate<String, Object> template = getRedisTemplate(clazz);

        Object cached = template.opsForValue().get(key);

        if (cached == null) {
            logger.debug("Cache miss for key [{}]", key);
            return null;
        } else {
            return objectMapper.convertValue(cached, clazz);
        }
    }

    public <T> List<T> getAsList(String key, Class<T> clazz) {
        RedisTemplate<String, Object> template = getRedisTemplate(clazz);

        Object cached = template.opsForValue().get(key);

        if (cached == null) {
            logger.debug("Cache miss for key [{}]", key);
            return null;
        }

        try {
            List<Object> raw = objectMapper.readValue(cached.toString(), new TypeReference<>() {
            });
            return raw.stream().map(s -> objectMapper.convertValue(s, clazz)).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to read data from cache for key [{}]. {}", key, e.getMessage());
            return null;
        }
    }

    public <T> boolean has(String key, Class<T> clazz) {
        RedisTemplate<String, Object> template = getRedisTemplate(clazz);

        return Boolean.TRUE.equals(template.hasKey(key));
    }

    public <T> boolean notHas(String key, Class<T> clazz) {
        return !has(key, clazz);
    }

    public <T> void evict(String key, Class<T> clazz) {
        RedisTemplate<String, Object> template = getRedisTemplate(clazz);

        template.delete(key);
    }

    /**
     * @param key                Cached key
     * @param clazz              Type of cached value
     * @param checkpointInSecond Must greater than zero
     * @return true if ttl <= checkpointInSecond
     * @implSpec https://redis.io/commands/ttl
     * @implSpec https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/core/RedisOperations.html#getExpire-K-
     */
    public <T> boolean isAboutToBeExpired(String key, Class<T> clazz, long checkpointInSecond) {
        RedisTemplate<String, Object> template = getRedisTemplate(clazz);

        Long ttlInSecond = template.getExpire(key);

        if (ttlInSecond == null || ttlInSecond <= -2) {
            return true;
        } else if (ttlInSecond == -1) {
            logger.warn("Cache entry with key = {} has no expiration set", key);
            return false;
        }

        return ttlInSecond <= checkpointInSecond;
    }

    private <T> RedisTemplate<String, Object> getRedisTemplate(Class<T> clazz) {
        RedisTemplate<String, Object> template = templates.get(clazz);

        if (template == null) {
            template = buildGenericJacksonRedisTemplate();
            templates.put(clazz, template);
        }

        return template;
    }

    protected RedisTemplate<String, Object> buildGenericJacksonRedisTemplate() {
        RedisSerializer<?> valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableDefaultSerializer(false);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
