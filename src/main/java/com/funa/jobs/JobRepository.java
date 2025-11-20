package com.funa.jobs;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Job entity.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByStatus(Job.Status status);

    @Query("select j.id from Job j where j.status = :status")
    List<UUID> findIdsByStatus(@Param("status") Job.Status status);
}
