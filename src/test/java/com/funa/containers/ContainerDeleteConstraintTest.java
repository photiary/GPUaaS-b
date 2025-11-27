package com.funa.containers;

import com.funa.containers.Container;
import com.funa.containers.ContainerEdge;
import com.funa.containers.ContainerEdgeRepository;
import com.funa.containers.ContainerRepository;
import com.funa.containers.ContainerService;
import com.funa.jobs.Job;
import com.funa.jobs.JobRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ContainerDeleteConstraintTest {

    @Autowired
    private ContainerService containerService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private ContainerEdgeRepository containerEdgeRepository;

    private UUID jobId;
    private UUID sourceContainerId;
    private UUID targetContainerId;

    @BeforeEach
    void setUp() {
        Job job = Job.builder().name("test-job").build();
        job = jobRepository.save(job);
        jobId = job.getId();

        Container source = Container.builder()
                .job(job)
                .label("source")
                .status(Container.Status.CREATED)
                .build();
        source = containerRepository.save(source);
        sourceContainerId = source.getId();

        Container target = Container.builder()
                .job(job)
                .label("target")
                .status(Container.Status.CREATED)
                .build();
        target = containerRepository.save(target);
        targetContainerId = target.getId();

        ContainerEdge edge = ContainerEdge.builder()
                .job(job)
                .sourceContainer(source)
                .targetContainer(target)
                .build();
        containerEdgeRepository.save(edge);
    }

    @AfterEach
    void tearDown() {
        containerEdgeRepository.deleteAll();
        containerRepository.deleteAll();
        jobRepository.deleteAll();
    }

    @Test
    void deleteContainer_ShouldSucceed_WhenEdgeExists() {
        // After fix, this should NOT throw exception
        assertDoesNotThrow(() -> {
            containerService.delete(sourceContainerId);
        });
    }
}
