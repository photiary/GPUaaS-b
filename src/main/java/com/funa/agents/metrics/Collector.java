package com.funa.agents.metrics;

public interface Collector {
    MetricsData collect(String containerId);
}
