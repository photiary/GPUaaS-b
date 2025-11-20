package com.funa.agents.metrics;

import com.funa.common.transport.Transporter;
import com.funa.containers.ContainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class MetricsCollectorAgent {

    private final Collector systemCollector;
    private final Collector gpuCollector;
    private final Aggregator aggregator;
    private final Transporter<List<MetricsData>> transporter;
    private final ContainerRepository containerRepository;

    /**
     * Class diagram compatibility: start(jobId) entrypoint.
     * Delegates to the existing start(jobId, containerIds) with an empty list
     * until a real container repository is integrated.
     */
    public void start(String jobId) {
        List<String> containerIds;
        try {
            UUID uuid = UUID.fromString(jobId);
            containerIds = containerRepository.findIdsByJobId(uuid)
                    .stream().map(UUID::toString).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Failed to resolve containers for jobId={}. Falling back to empty list.", jobId, e);
            containerIds = java.util.Collections.emptyList();
        }
        start(jobId, containerIds);
    }

    /**
     * Start a one-shot metrics collection for a job and send aggregated result.
     * In a real implementation this would be scheduled and query container IDs from repository.
     */
    public void start(String jobId, List<String> containerIds) {
        log.info("Starting metrics collection for jobId={} containers={}", jobId, containerIds);
        List<MetricsData> aggregated = new ArrayList<>();
        for (String cid : containerIds) {
            List<MetricsData> parts = new ArrayList<>();
            parts.add(systemCollector.collect(cid));
            parts.add(gpuCollector.collect(cid));
            MetricsData merged = aggregator.aggregate(parts);
            if (merged != null) aggregated.add(merged);
        }
        transporter.send(jobId, aggregated);
    }

    public void stop(String jobId) {
        log.info("Stopping metrics collection for jobId={}", jobId);
    }
}
