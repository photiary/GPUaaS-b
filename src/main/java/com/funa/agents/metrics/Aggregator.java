package com.funa.agents.metrics;

import java.time.Instant;
import java.util.List;

/**
 * Simple aggregator that merges multiple MetricsData of the same container into one.
 * Non-zero values take precedence; otherwise values are summed where applicable.
 */
public class Aggregator {
    public MetricsData aggregate(List<MetricsData> dataList) {
        if (dataList == null || dataList.isEmpty()) return null;
        String containerId = dataList.get(0).getContainerId();
        double cpu = 0.0;
        double gpu = 0.0;
        double mem = 0.0;
        Instant ts = null;
        for (MetricsData d : dataList) {
            if (d == null) continue;
            if (d.getCpuUsage() != 0.0) cpu = d.getCpuUsage();
            if (d.getGpuUsage() != 0.0) gpu = d.getGpuUsage();
            if (d.getMemoryUsage() != 0.0) mem = d.getMemoryUsage();
            if (d.getTimestamp() != null) ts = d.getTimestamp();
        }
        return MetricsData.builder()
                .containerId(containerId)
                .timestamp(ts != null ? ts : Instant.now())
                .cpuUsage(cpu)
                .gpuUsage(gpu)
                .memoryUsage(mem)
                .build();
    }
}
