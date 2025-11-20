package com.funa.containers;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Container entity.
 */
@Repository
public interface ContainerRepository extends JpaRepository<Container, UUID> {
}
