package com.funa.containers;

import com.funa.containers.dto.ContainerRequest;
import com.funa.containers.dto.ContainerResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Repository (임시 In-Memory)
 * App Container Node 엔티티 설계 기반 스텁 구현.
 */
@Repository
public class ContainerRepository {
  private final Map<UUID, ContainerResponse> store = new ConcurrentHashMap<>();

  public ContainerResponse save(UUID id, ContainerRequest req) {
    ContainerResponse value = ContainerResponse.from(req, id);
    store.put(id, value);
    return value;
  }

  public Optional<ContainerResponse> findById(UUID id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<ContainerResponse> findAll() {
    return new ArrayList<>(store.values());
  }

  public void deleteById(UUID id) {
    store.remove(id);
  }
}
