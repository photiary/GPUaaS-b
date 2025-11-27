package com.funa.containers;

import com.funa.jobs.Job;
import com.funa.jobs.JobRepository;
import com.funa.jobs.JobService;
import com.funa.jobs.dto.JobRequest;
import com.funa.jobs.dto.JobResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ContainerEdgeNullTest {

  @Autowired
  private JobService jobService;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private ContainerEdgeRepository containerEdgeRepository;

  @Autowired
  private JobRepository jobRepository;

  @Test
  @Transactional
  void createEdgeWithNullKey_ShouldSucceed() {
    // 1. Job 생성
    JobRequest jobRequest = JobRequest.builder()
        .name("test-job-null-edge")
        .build();
    JobResponse jobResponse = jobService.create(jobRequest);
    UUID jobId = jobResponse.getId();

    // 2. Container 생성
    Job job = jobRepository.findById(jobId).orElseThrow();
    
    Container container = Container.builder()
        .label("test-container-null-edge")
        .job(job)
        .status(Container.Status.CREATED)
        .build();
    container = containerRepository.save(container);

    // 3. ContainerEdge 생성 (edgeKey is NULL)
    ContainerEdge edge = ContainerEdge.builder()
        .job(job)
        .sourceContainer(container)
        .targetContainer(container)
        .edgeKey(null) // Explicitly null
        .label("test-label")
        .isActive(true)
        .build();
    
    // This should fail initially if edgeKey is not nullable
    assertDoesNotThrow(() -> containerEdgeRepository.saveAndFlush(edge));
  }
}
