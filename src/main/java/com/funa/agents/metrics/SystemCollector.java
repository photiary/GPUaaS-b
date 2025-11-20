package com.funa.agents.metrics;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class SystemCollector implements Collector {
    @Override
    public MetricsData collect(String containerId) {
        // Dummy data for now. In real implementation, fetch from node/container runtime.
        double cpu = ThreadLocalRandom.current().nextDouble(0, 100);
        double mem = ThreadLocalRandom.current().nextDouble(0, 100);
        MetricsData data = MetricsData.builder()
                .containerId(containerId)
                .timestamp(Instant.now())
                .cpuUsage(cpu)
                .memoryUsage(mem)
                .gpuUsage(0.0)
                .build();
        log.debug("SystemCollector collected: {}", data);
        return data;
    }
}
