package com.funa.nodes;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Node entity.
 */
@Repository
public interface NodeRepository extends JpaRepository<Node, UUID> {
}
