package com.funa.agents.state;

import com.funa.jobs.Job;
import com.funa.jobs.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * High-level manager that periodically enumerates active jobs and dispatches per-job monitoring.
 */
@Slf4j
@RequiredArgsConstructor
public class StateMonitorAgent {

    private final AppStateMonitorAgent appStateMonitorAgent;
    private final JobRepository jobRepository;

    public void startAll() {
        log.debug("StateMonitorAgent.startAll invoked");
        for (String jobId : getActiveJobIds()) {
            dispatchJobMonitoring(jobId);
        }
    }

    public void stopAll() {
        log.info("StateMonitorAgent stopAll() invoked");
    }

    protected List<String> getActiveJobIds() {
        try {
            return jobRepository.findByStatus(Job.Status.RUNNING).stream()
                    .map(j -> j.getId().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Failed to load active jobs. Fallback to empty list.", e);
            return Collections.emptyList();
        }
    }

    protected void dispatchJobMonitoring(String jobId) {
        // Minimal stub container list. Real impl would resolve containers by job.
        appStateMonitorAgent.start(jobId, Collections.emptyList());
    }
}
