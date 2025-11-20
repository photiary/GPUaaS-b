package com.funa.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSE 서비스: Redis Stream(metrics:job:{jobId})을 블로킹으로 구독하여 클라이언트로 전달.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsSseService {

    private final StringRedisTemplate redisTemplate;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private String streamKey(UUID jobId) {
        return "metrics:job:" + jobId;
    }

    public SseEmitter subscribeMetrics(UUID jobId) {
        SseEmitter emitter = new SseEmitter(0L); // never timeout from server side
        String key = streamKey(jobId);
        AtomicBoolean active = new AtomicBoolean(true);

        emitter.onTimeout(() -> {
            active.set(false);
            log.info("SSE timeout for jobId={}", jobId);
        });
        emitter.onError(ex -> {
            active.set(false);
            log.warn("SSE error for jobId={}: {}", jobId, ex.toString());
        });
        emitter.onCompletion(() -> {
            active.set(false);
            log.info("SSE completed for jobId={}", jobId);
        });

        // 최초 연결 확인용 ping
        try {
            emitter.send(SseEmitter.event().name("ping").data("ok"));
        } catch (IOException e) {
            log.warn("Failed to send initial ping for jobId={}", jobId, e);
        }

        executor.submit(() -> consumeLoop(key, emitter, active));
        return emitter;
    }

    @SuppressWarnings("unchecked")
    private void consumeLoop(String key, SseEmitter emitter, AtomicBoolean active) {
        var ops = redisTemplate.opsForStream();
        String lastId = null;
        log.info("Start consuming Redis Stream key={}", key);
        while (active.get()) {
            try {
                var options = StreamReadOptions.empty().block(java.time.Duration.ofSeconds(10)).count(10);
                List<org.springframework.data.redis.connection.stream.MapRecord<String, Object, Object>> records =
                        ops.read(options, StreamOffset.create(key, lastId == null ? ReadOffset.latest() : ReadOffset.from(lastId)));

                if (records == null || records.isEmpty()) {
                    // 주기적으로 keep-alive 전송
                    try {
                        emitter.send(SseEmitter.event().name("keepalive").data("tick"));
                    } catch (IOException ignore) { }
                    try { Thread.sleep(500L); } catch (InterruptedException ignored) { }
                    continue;
                }

                for (org.springframework.data.redis.connection.stream.MapRecord<String, Object, Object> record : records) {
                    lastId = record.getId().getValue();
                    Object val = record.getValue().getOrDefault("data", "{}");
                    String payload = String.valueOf(val);
                    try {
                        emitter.send(SseEmitter.event()
                                .name("metrics")
                                .id(record.getId().getValue())
                                .data(payload)
                        );
                    } catch (IOException e) {
                        log.warn("Failed to send SSE for key={}, id={}", key, record.getId(), e);
                        active.set(false);
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn("Error while reading Redis Stream key={}", key, e);
                // 잠시 대기 후 재시도
                try { Thread.sleep(1000L); } catch (InterruptedException ignored) { }
            }
        }
        try { emitter.complete(); } catch (Exception ignored) {}
        log.info("Stop consuming Redis Stream key={}", key);
    }
}
