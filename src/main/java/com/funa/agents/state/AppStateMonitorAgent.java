package com.funa.agents.state;

import com.funa.common.transport.Transporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AppStateMonitorAgent {

    private final StateCollector stateCollector;
    private final Transporter<JobStateData> transporter;

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

    public void stop(String jobId) {
        log.info("Stopping state monitor for jobId={}", jobId);
    }
}
