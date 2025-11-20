package com.funa.agents.state;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class AppContainerStateCollector implements StateCollector {
    @Override
    public ContainerStateData collect(String containerId) {
        // Dummy state for now.
        ContainerStateData data = ContainerStateData.builder()
                .containerId(containerId)
                .state("RUNNING")
                .timestamp(Instant.now())
                .build();
        log.debug("Collected container state: {}", data);
        return data;
    }
}
