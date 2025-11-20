package com.funa.common.transport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

/**
 * Redis Stream transporter that builds a stream key and XADDs serialized payloads.
 * Falls back to logging only when RedisTemplate is not provided or on failure.
 */
@Slf4j
@RequiredArgsConstructor
public class RedisStreamTransporter<T> implements Transporter<T> {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate; // can be null in tests or minimal wiring

    /**
     * Prefix for the Redis stream key, e.g., "metrics:job" or "state:job".
     */
    @Setter
    private String streamKeyPrefix = "metrics:job";

    @Override
    public void send(String jobId, T data) {
        String key = buildStreamKey(jobId);
        String payload = serialize(data);
        if (redisTemplate == null) {
            log.info("[RedisStub] XADD {} data={{data:{}}}", key, payload);
            return;
        }
        try {
            MapRecord<String, String, String> record = MapRecord.create(key, Map.of("data", payload));
            var recordId = redisTemplate.opsForStream().add(record);
            log.debug("XADD to RedisStream done. key={}, recordId={}, payload={}", key, recordId, payload);
        } catch (Exception e) {
            log.warn("Failed to XADD to RedisStream. Falling back to log. key={}, data={}", key, payload, e);
            log.info("[RedisFallback] XADD {} data={{data:{}}}", key, payload);
        }
    }

    private String buildStreamKey(String jobId) {
        return String.format("%s:%s", streamKeyPrefix, jobId);
    }

    private String serialize(T data) {
        if (data == null) return "null";
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize data. Fallback to toString().", e);
            return String.valueOf(data);
        }
    }
}
