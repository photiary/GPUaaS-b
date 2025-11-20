package com.funa.nodes;

import com.funa.nodes.dto.NodeRequest;
import com.funa.nodes.dto.NodeResponse;
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
    UUID id = UUID.randomUUID();
    return repository.save(id, request);
  }

  public Optional<NodeResponse> findById(UUID id) {
    return repository.findById(id);
  }

  public List<NodeResponse> findAll() {
    return repository.findAll();
  }

  public Optional<NodeResponse> update(UUID id, NodeRequest request) {
    return repository.findById(id).map(existing -> repository.save(id, request));
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }
}
