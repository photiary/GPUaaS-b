package com.funa.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funa.agents.metrics.Aggregator;
import com.funa.agents.metrics.GPUCollector;
import com.funa.agents.metrics.MetricsCollectorAgent;
import com.funa.agents.metrics.SystemCollector;
import com.funa.agents.state.AppContainerStateCollector;
import com.funa.agents.state.AppStateMonitorAgent;
import com.funa.agents.state.StateMonitorAgent;
import com.funa.common.transport.RedisStreamTransporter;
import com.funa.common.transport.Transporter;
import com.funa.containers.ContainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import com.funa.jobs.JobRepository;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class AgentsConfig {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate; // auto-configured by Spring Boot

    // Transporters
    @Bean
    public Transporter<List<com.funa.agents.metrics.MetricsData>> metricsTransporter() {
        RedisStreamTransporter<List<com.funa.agents.metrics.MetricsData>> t =
                new RedisStreamTransporter<>(objectMapper, stringRedisTemplate);
        t.setStreamKeyPrefix(com.funa.agents.metrics.MetricsCollectorAgent.STREAM_KEY_PREFIX);
        return t;
    }

    @Bean
    public Transporter<com.funa.agents.state.JobStateData> stateTransporter() {
        RedisStreamTransporter<com.funa.agents.state.JobStateData> t =
                new RedisStreamTransporter<>(objectMapper, stringRedisTemplate);
        t.setStreamKeyPrefix(com.funa.agents.state.AppStateMonitorAgent.STREAM_KEY_PREFIX);
        return t;
    }

    // Collectors and helpers
    @Bean
    public SystemCollector systemCollector() { return new SystemCollector(); }

    @Bean
    public GPUCollector gpuCollector() { return new GPUCollector(); }

    @Bean
    public Aggregator aggregator() { return new Aggregator(); }

    @Bean
    public AppContainerStateCollector appContainerStateCollector() { return new AppContainerStateCollector(); }

    // Agents
    @Bean
    public MetricsCollectorAgent metricsCollectorAgent(SystemCollector sys, GPUCollector gpu,
                                                       Aggregator aggregator,
                                                       Transporter<List<com.funa.agents.metrics.MetricsData>> transporter,
                                                       ContainerRepository containerRepository) {
        return new MetricsCollectorAgent(sys, gpu, aggregator, transporter, containerRepository);
    }

    @Bean
    public AppStateMonitorAgent appStateMonitorAgent(AppContainerStateCollector collector,
                                                     Transporter<com.funa.agents.state.JobStateData> transporter,
                                                     ContainerRepository containerRepository) {
        return new AppStateMonitorAgent(collector, transporter, containerRepository);
    }

    @Bean
    public StateMonitorAgent stateMonitorAgent(AppStateMonitorAgent appStateMonitorAgent,
                                              JobRepository jobRepository) {
        return new StateMonitorAgent(appStateMonitorAgent, jobRepository);
    }
}
