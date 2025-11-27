package com.funa.jobs;

import com.funa.containers.Container;
import com.funa.containers.ContainerEdge;
import com.funa.containers.ContainerEdgeRepository;
import com.funa.containers.ContainerRepository;
import com.funa.jobs.dto.JobRequest;
import com.funa.jobs.dto.JobResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JobDeleteTest {

  @Autowired
  private JobService jobService;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private ContainerEdgeRepository containerEdgeRepository;

  @Autowired
  private JobRepository jobRepository;

  @Test
  void deleteJobWithContainersAndEdges_ShouldSucceed() {
    // 1. Job 생성
    JobRequest jobRequest = JobRequest.builder()
        .name("test-job")
        .build();
    JobResponse jobResponse = jobService.create(jobRequest);
    UUID jobId = jobResponse.getId();

    // 2. Container 생성 및 Job 연결
    Job job = jobRepository.findById(jobId).orElseThrow();
    
    Container container = Container.builder()
        .label("test-container")
        .job(job)
        .status(Container.Status.CREATED)
        .createdAt(java.time.OffsetDateTime.now())
        .build();
    container = containerRepository.save(container);
    UUID containerId = container.getId();

    // 3. ContainerEdge 생성
    ContainerEdge edge = ContainerEdge.builder()
        .job(job)
        .sourceContainer(container)
        .targetContainer(container)
        .edgeKey("test-edge")
        .isActive(true)
        .createdAt(java.time.OffsetDateTime.now())
        .build();
    edge = containerEdgeRepository.save(edge);
    UUID edgeId = edge.getId();

    // 4. Job 삭제
    jobService.delete(jobId);

    // 5. 검증
    assertTrue(jobRepository.findById(jobId).isEmpty(), "Job should be deleted");
    assertTrue(containerRepository.findById(containerId).isEmpty(), "Container should be deleted via cascade");
    assertTrue(containerEdgeRepository.findById(edgeId).isEmpty(), "Edge should be deleted via cascade");
  }
}
