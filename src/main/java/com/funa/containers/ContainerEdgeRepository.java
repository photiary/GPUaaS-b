package com.funa.containers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerEdgeRepository extends JpaRepository<ContainerEdge, UUID> {
  List<ContainerEdge> findByJob_Id(UUID jobId);
  Optional<ContainerEdge> findByIdAndJob_Id(UUID id, UUID jobId);
  void deleteByIdAndJob_Id(UUID id, UUID jobId);
}
