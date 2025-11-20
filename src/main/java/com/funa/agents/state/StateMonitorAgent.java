package com.funa.agents.state;

import com.funa.jobs.Job;
import com.funa.jobs.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    protected List<String> getActiveJobIds() {
        try {
            // 모든 Job의 ID를 조회하도록 수정
            return jobRepository.findAll().stream()
                    .map(Job::getId)
                    .map(java.util.UUID::toString)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Failed to load active jobs. Fallback to empty list.", e);
            return Collections.emptyList();
        }
    }

    protected void dispatchJobMonitoring(String jobId) {
        // 컨테이너 목록을 저장소에서 조회하도록 AppStateMonitorAgent가 처리
        appStateMonitorAgent.start(jobId);
    }
}
