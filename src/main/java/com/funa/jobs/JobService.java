package com.funa.jobs;

import com.funa.jobs.dto.JobRequest;
import com.funa.jobs.dto.JobResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

  private final JobRepository repository;

  public JobResponse create(JobRequest request) {
    Job entity = toEntityForCreate(request);
    Job saved = repository.save(entity);
    return toResponse(saved);
  }

  public Optional<JobResponse> findById(UUID id) {
    return repository.findById(id).map(this::toResponse);
  }

  public List<JobResponse> findAll() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  public Optional<JobResponse> update(UUID id, JobRequest request) {
    return repository.findById(id).map(e -> {
      e.setName(request.getName());
      e.setDescription(request.getDescription());
      e.setRequestedGpus(nvl(request.getRequestedGpus()));
      e.setRequestedCpus(nvl(request.getRequestedCpus()));
      e.setRequestedMemory(nvl(request.getRequestedMemory()));
      e.setMetadata(request.getMetadata());
      return toResponse(repository.save(e));
    });
  }

  public void delete(UUID id) {
    repository.deleteById(id);
  }

  private Job toEntityForCreate(JobRequest req) {
    return Job.builder()
        .name(req.getName())
        .description(req.getDescription())
        .status(Job.Status.STOPPED)
        .submitTime(OffsetDateTime.now())
        .requestedGpus(nvl(req.getRequestedGpus()))
        .requestedCpus(nvl(req.getRequestedCpus()))
        .requestedMemory(nvl(req.getRequestedMemory()))
        .metadata(req.getMetadata())
        .build();
  }

  private JobResponse toResponse(Job e) {
    return JobResponse.builder()
        .id(e.getId())
        .name(e.getName())
        .description(e.getDescription())
        .status(e.getStatus() != null ? e.getStatus().name() : null)
        .submitTime(e.getSubmitTime())
        .startTime(e.getStartTime())
        .endTime(e.getEndTime())
        .requestedGpus(e.getRequestedGpus())
        .requestedCpus(e.getRequestedCpus())
        .requestedMemory(e.getRequestedMemory())
        .metadata(e.getMetadata())
        .build();
  }

  private int nvl(Integer v) {
    return v == null ? 0 : v;
  }
}
