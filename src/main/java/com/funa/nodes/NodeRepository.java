package com.funa.nodes;

import com.funa.nodes.dto.NodeRequest;
import com.funa.nodes.dto.NodeResponse;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Repository (임시 In-Memory)
 * Resource Node 엔티티 설계 기반 스텁 구현.
 */
@Repository
public class NodeRepository {
  private final Map<UUID, NodeResponse> store = new ConcurrentHashMap<>();

  public NodeResponse save(UUID id, NodeRequest req) {
    NodeResponse value = NodeResponse.from(req, id, OffsetDateTime.now());
    store.put(id, value);
    return value;
  }

  public Optional<NodeResponse> findById(UUID id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<NodeResponse> findAll() {
    return new ArrayList<>(store.values());
  }

  public void deleteById(UUID id) {
    store.remove(id);
  }
}
