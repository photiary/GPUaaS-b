package com.funa.containers;

import com.funa.containers.dto.ContainerEdgeRequest;
import com.funa.containers.dto.ContainerEdgeResponse;
import com.funa.jobs.Job;
import com.funa.jobs.JobRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContainerEdgeService {

  private final ContainerEdgeRepository edgeRepository;
  private final JobRepository jobRepository;
  private final ContainerRepository containerRepository;

  public ContainerEdgeService(
      ContainerEdgeRepository edgeRepository,
      JobRepository jobRepository,
      ContainerRepository containerRepository) {
    this.edgeRepository = edgeRepository;
    this.jobRepository = jobRepository;
    this.containerRepository = containerRepository;
  }

  public ContainerEdgeResponse create(UUID jobId, ContainerEdgeRequest request) {
    Job job = jobRepository.findById(jobId)
        .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

    Container source = containerRepository.findById(request.getSourceContainerId())
        .orElseThrow(() -> new IllegalArgumentException("Source container not found: " + request.getSourceContainerId()));
    Container target = containerRepository.findById(request.getTargetContainerId())
        .orElseThrow(() -> new IllegalArgumentException("Target container not found: " + request.getTargetContainerId()));

    // 동일 Job 소속 검증
    if (source.getJob() == null || target.getJob() == null ||
        !jobId.equals(source.getJob().getId()) || !jobId.equals(target.getJob().getId())) {
      throw new IllegalArgumentException("Containers must belong to the same job: " + jobId);
    }

    ContainerEdge edge = ContainerEdge.builder()
        .job(job)
        .sourceContainer(source)
        .targetContainer(target)
        .edgeKey(request.getEdgeKey())
        .label(request.getLabel())
        .condition(request.getCondition())
        .isActive(request.getIsActive() != null ? request.getIsActive() : Boolean.TRUE)
        .build();

    ContainerEdge saved = edgeRepository.save(edge);
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public List<ContainerEdgeResponse> list(UUID jobId) {
    return edgeRepository.findByJob_Id(jobId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Optional<ContainerEdgeResponse> get(UUID jobId, UUID edgeId) {
    return edgeRepository.findByIdAndJob_Id(edgeId, jobId).map(this::toResponse);
  }

  public Optional<ContainerEdgeResponse> update(UUID jobId, UUID edgeId, ContainerEdgeRequest request) {
    return edgeRepository.findByIdAndJob_Id(edgeId, jobId).map(edge -> {
      if (request.getEdgeKey() != null) edge.setEdgeKey(request.getEdgeKey());
      if (request.getLabel() != null) edge.setLabel(request.getLabel());
      if (request.getCondition() != null) edge.setCondition(request.getCondition());
      if (request.getIsActive() != null) edge.setIsActive(request.getIsActive());

      // source/target 변경 시 검증
      if (request.getSourceContainerId() != null &&
          !request.getSourceContainerId().equals(edge.getSourceContainer().getId())) {
        Container src = containerRepository.findById(request.getSourceContainerId())
            .orElseThrow(() -> new IllegalArgumentException("Source container not found: " + request.getSourceContainerId()));
        if (src.getJob() == null || !jobId.equals(src.getJob().getId()))
          throw new IllegalArgumentException("Source container must belong to job: " + jobId);
        edge.setSourceContainer(src);
      }
      if (request.getTargetContainerId() != null &&
          !request.getTargetContainerId().equals(edge.getTargetContainer().getId())) {
        Container tgt = containerRepository.findById(request.getTargetContainerId())
            .orElseThrow(() -> new IllegalArgumentException("Target container not found: " + request.getTargetContainerId()));
        if (tgt.getJob() == null || !jobId.equals(tgt.getJob().getId()))
          throw new IllegalArgumentException("Target container must belong to job: " + jobId);
        edge.setTargetContainer(tgt);
      }

      return toResponse(edge);
    });
  }

  public void delete(UUID jobId, UUID edgeId) {
    edgeRepository.deleteByIdAndJob_Id(edgeId, jobId);
  }

  private ContainerEdgeResponse toResponse(ContainerEdge e) {
    return ContainerEdgeResponse.builder()
        .id(e.getId())
        .jobId(e.getJob() != null ? e.getJob().getId() : null)
        .sourceContainerId(e.getSourceContainer() != null ? e.getSourceContainer().getId() : null)
        .targetContainerId(e.getTargetContainer() != null ? e.getTargetContainer().getId() : null)
        .edgeKey(e.getEdgeKey())
        .label(e.getLabel())
        .condition(e.getCondition())
        .isActive(e.getIsActive())
        .createdAt(e.getCreatedAt())
        .updatedAt(e.getUpdatedAt())
        .build();
  }
}
