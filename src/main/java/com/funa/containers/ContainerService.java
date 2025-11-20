package com.funa.containers;

import com.funa.containers.dto.ContainerRequest;
import com.funa.containers.dto.ContainerResponse;
import com.funa.jobs.Job;
import com.funa.jobs.JobRepository;
import com.funa.nodes.Node;
import com.funa.nodes.NodeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContainerService {

  private final ContainerRepository repository;
  private final JobRepository jobRepository;
  private final NodeRepository nodeRepository;

  public ContainerResponse create(ContainerRequest request) {
    Container entity = toEntityForCreate(request);
    Container saved = repository.save(entity);
    return toResponse(saved);
  }

  public Optional<ContainerResponse> findById(UUID id) {
    return repository.findById(id).map(this::toResponse);
  }

  public List<ContainerResponse> findAll() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  public Optional<ContainerResponse> update(UUID id, ContainerRequest request) {
    return repository.findById(id).map(existing -> {
      applyUpdate(existing, request);
      Container saved = repository.save(existing);
      return toResponse(saved);
    });
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }

  private Container toEntityForCreate(ContainerRequest req) {
    Container.Status status = parseStatus(req.getStatus());
    Job job = null;
    if (req.getJobId() != null) {
      job = jobRepository.findById(req.getJobId())
          .orElseThrow(() -> new IllegalArgumentException("Invalid jobId: " + req.getJobId()));
    }
    Node node = null;
    if (req.getNodeId() != null) {
      node = nodeRepository.findById(req.getNodeId())
          .orElseThrow(() -> new IllegalArgumentException("Invalid nodeId: " + req.getNodeId()));
    }
    return Container.builder()
        .job(job)
        .node(node)
        .label(req.getLabel())
        .description(req.getDescription())
        .sequence(req.getSequence())
        .status(status != null ? status : Container.Status.CREATED)
        .config(req.getConfig())
        .positionX(req.getPositionX())
        .positionY(req.getPositionY())
        .build();
  }

  private void applyUpdate(Container entity, ContainerRequest req) {
    if (req.getLabel() != null) entity.setLabel(req.getLabel());
    if (req.getDescription() != null) entity.setDescription(req.getDescription());
    if (req.getSequence() != null) entity.setSequence(req.getSequence());
    if (req.getStatus() != null) entity.setStatus(parseStatus(req.getStatus()));
    if (req.getConfig() != null) entity.setConfig(req.getConfig());
    if (req.getPositionX() != null) entity.setPositionX(req.getPositionX());
    if (req.getPositionY() != null) entity.setPositionY(req.getPositionY());
  }

  private ContainerResponse toResponse(Container c) {
    return ContainerResponse.builder()
        .id(c.getId())
        .label(c.getLabel())
        .description(c.getDescription())
        .sequence(c.getSequence())
        .status(c.getStatus() != null ? c.getStatus().name() : null)
        .config(c.getConfig())
        .positionX(c.getPositionX())
        .positionY(c.getPositionY())
        .build();
  }

  private Container.Status parseStatus(String status) {
    if (status == null) return null;
    try { return Container.Status.valueOf(status.toUpperCase()); } catch (Exception e) { return null; }
  }
}
