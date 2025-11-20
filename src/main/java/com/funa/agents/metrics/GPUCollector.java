package com.funa.agents.metrics;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class GPUCollector implements Collector {
    @Override
    public MetricsData collect(String containerId) {
        double gpu = ThreadLocalRandom.current().nextDouble(0, 100);
        MetricsData data = MetricsData.builder()
                .containerId(containerId)
                .timestamp(Instant.now())
                .gpuUsage(gpu)
                .cpuUsage(0.0)
                .memoryUsage(0.0)
                .build();
        log.debug("GPUCollector collected: {}", data);
        return data;
    }
}
