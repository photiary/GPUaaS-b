package com.funa.agents;

import com.funa.agents.metrics.MetricsCollectorAgent;
import com.funa.agents.state.StateMonitorAgent;
import com.funa.jobs.Job;
import com.funa.jobs.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Spring @Scheduled tasks that trigger agent workflows as shown in the sequence diagrams.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerTasks {

    private final StateMonitorAgent stateMonitorAgent;
    private final MetricsCollectorAgent metricsCollectorAgent;
    private final JobRepository jobRepository;

    /**
     * 주기적으로 전체 Job 상태 모니터링을 수행 (StateMonitorAgent)
     */
    @Scheduled(fixedDelayString = "${agents.state.fixed-delay:5000}")
    public void scheduleStateMonitoring() {
        log.trace("[Scheduler] scheduleStateMonitoring tick");
        stateMonitorAgent.startAll();
    }

    /**
     * 주기적으로 RUNNING Job의 메트릭 수집을 수행 (MetricsCollectorAgent)
     * 현재는 컨테이너 ID를 별도 저장소에서 조회하지 않아 빈 리스트로 전달합니다.
     */
    @Scheduled(fixedDelayString = "${agents.metrics.fixed-delay:5000}")
    public void scheduleMetricsCollection() {
        log.trace("[Scheduler] scheduleMetricsCollection tick");
        jobRepository.findByStatus(Job.Status.RUNNING).forEach(job -> {
            String jobId = job.getId().toString();
            metricsCollectorAgent.start(jobId, Collections.emptyList());
        });
    }
}
