package com.funa.agents.state;

import com.funa.common.transport.Transporter;
import com.funa.containers.ContainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AppStateMonitorAgent {

    /**
     * Redis Stream key prefix used by this agent to publish job/container states.
     * Example final key: state:job:{jobId}
     */
    public static final String STREAM_KEY_PREFIX = "state:job";

    private final StateCollector stateCollector;
    private final Transporter<JobStateData> transporter;
    private final ContainerRepository containerRepository;

    /**
     * One-shot collection of all container states for a job.
     */
    public void start(String jobId, List<String> containerIds) {
        log.info("Starting state monitor for jobId={} containers={}", jobId, containerIds);
        List<ContainerStateData> list = new ArrayList<>();
        for (String cid : containerIds) {
            list.add(stateCollector.collect(cid));
        }
        JobStateData jobState = JobStateData.builder()
                .jobId(jobId)
                .containers(list)
                .timestamp(Instant.now())
                .build();
        transporter.send(jobId, jobState);
    }

    /**
     * Class diagram compatibility: start(jobId) entrypoint.
     * Resolve container IDs by jobId like MetricsCollectorAgent and delegate.
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

    public void stop(String jobId) {
        log.info("Stopping state monitor for jobId={}", jobId);
    }
}
