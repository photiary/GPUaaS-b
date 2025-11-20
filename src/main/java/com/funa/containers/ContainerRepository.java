package com.funa.containers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Container entity.
 */
@Repository
public interface ContainerRepository extends JpaRepository<Container, UUID> {

    /**
     * Returns container IDs that belong to the given job.
     */
    @Query("select c.id from Container c where c.job.id = :jobId")
    List<UUID> findIdsByJobId(@Param("jobId") UUID jobId);
}
