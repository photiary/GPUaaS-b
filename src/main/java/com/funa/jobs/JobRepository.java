package com.funa.jobs;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Job entity.
 */
@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
}
