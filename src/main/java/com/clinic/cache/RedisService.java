package com.clinic.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Store value with expiry
    public void set(String key, Object value, long timeoutInSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutInSeconds, TimeUnit.SECONDS);
    }

    // Store value without expiry
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Get value
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Delete key
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Check if key exists
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Cache keys
    public static final String DOCTOR_CACHE_KEY = "doctor:";
    public static final String PATIENT_CACHE_KEY = "patient:";
    public static final String AVAILABLE_DOCTORS_KEY = "doctors:available";
    public static final long CACHE_TTL = 300; // 5 minutes
}
