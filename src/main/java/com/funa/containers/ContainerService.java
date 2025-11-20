package com.funa.containers;

import com.funa.containers.dto.ContainerRequest;
import com.funa.containers.dto.ContainerResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContainerService {

  private final ContainerRepository repository;

  public ContainerResponse create(ContainerRequest request) {
    UUID id = UUID.randomUUID();
    return repository.save(id, request);
  }

  public Optional<ContainerResponse> findById(UUID id) {
    return repository.findById(id);
  }

  public List<ContainerResponse> findAll() {
    return repository.findAll();
  }

  public Optional<ContainerResponse> update(UUID id, ContainerRequest request) {
    return repository.findById(id).map(existing -> repository.save(id, request));
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }
}
