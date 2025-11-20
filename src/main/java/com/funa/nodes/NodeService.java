package com.funa.nodes;

import com.funa.nodes.dto.NodeRequest;
import com.funa.nodes.dto.NodeResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeService {

  private final NodeRepository repository;

  public NodeResponse create(NodeRequest request) {
    Node entity = toEntityForCreate(request);
    Node saved = repository.save(entity);
    return toResponse(saved);
  }

  public Optional<NodeResponse> findById(UUID id) {
    return repository.findById(id).map(this::toResponse);
  }

  public List<NodeResponse> findAll() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  public Optional<NodeResponse> update(UUID id, NodeRequest request) {
    return repository.findById(id).map(existing -> {
      applyUpdate(existing, request);
      Node saved = repository.save(existing);
      return toResponse(saved);
    });
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }

  private Node toEntityForCreate(NodeRequest req) {
    Node.Type type = parseType(req.getType());
    Node.Status status = parseStatus(req.getStatus());
    return Node.builder()
        .name(req.getName())
        .type(type)
        .gpuCount(req.getGpuCount())
        .cpuCores(req.getCpuCores())
        .memoryCapacity(req.getMemoryCapacity())
        .status(status != null ? status : Node.Status.ONLINE)
        .lastHeartbeat(OffsetDateTime.now())
        .build();
  }

  private void applyUpdate(Node entity, NodeRequest req) {
    if (req.getName() != null) entity.setName(req.getName());
    if (req.getType() != null) entity.setType(parseType(req.getType()));
    if (req.getGpuCount() != null) entity.setGpuCount(req.getGpuCount());
    if (req.getCpuCores() != null) entity.setCpuCores(req.getCpuCores());
    if (req.getMemoryCapacity() != null) entity.setMemoryCapacity(req.getMemoryCapacity());
    if (req.getStatus() != null) entity.setStatus(parseStatus(req.getStatus()));
    entity.setLastHeartbeat(OffsetDateTime.now());
  }

  private NodeResponse toResponse(Node n) {
    return NodeResponse.builder()
        .id(n.getId())
        .name(n.getName())
        .type(n.getType() != null ? n.getType().name() : null)
        .gpuCount(n.getGpuCount())
        .cpuCores(n.getCpuCores())
        .memoryCapacity(n.getMemoryCapacity())
        .status(n.getStatus() != null ? n.getStatus().name() : null)
        .lastHeartbeat(n.getLastHeartbeat())
        .build();
  }

  private Node.Type parseType(String type) {
    if (type == null) return null;
    try { return Node.Type.valueOf(type.toUpperCase()); } catch (Exception e) { return null; }
  }

  private Node.Status parseStatus(String status) {
    if (status == null) return null;
    try { return Node.Status.valueOf(status.toUpperCase()); } catch (Exception e) { return null; }
  }
}
