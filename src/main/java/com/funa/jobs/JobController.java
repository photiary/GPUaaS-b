package com.funa.jobs;

import com.funa.jobs.dto.JobRequest;
import com.funa.jobs.dto.JobResponse;
import com.funa.jobs.dto.SimpleContainerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Job 관리 및 모니터링 API")
public class JobController {

  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @Operation(summary = "새 Job 생성")
  @PostMapping
  public ResponseEntity<JobResponse> create(@Validated @RequestBody JobRequest request) {
    JobResponse res = jobService.create(request);
    return ResponseEntity.ok(res);
  }

  @Operation(summary = "Job 상세 조회")
  @GetMapping("/{jobId}")
  public ResponseEntity<JobResponse> get(@PathVariable UUID jobId) {
    return jobService.findById(jobId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "Job 수정")
  @PutMapping("/{jobId}")
  public ResponseEntity<JobResponse> update(@PathVariable UUID jobId, @Validated @RequestBody JobRequest request) {
    return jobService.update(jobId, request)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "Job 삭제")
  @DeleteMapping("/{jobId}")
  public ResponseEntity<Void> delete(@PathVariable UUID jobId) {
    jobService.delete(jobId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Job 목록 조회")
  @GetMapping
  public ResponseEntity<List<JobResponse>> list() {
    return ResponseEntity.ok(jobService.findAll());
  }

  @Operation(summary = "전체 Job 상태 실시간 모니터링 (SSE)")
  @GetMapping("/monitor")
  public SseEmitter monitorAll() throws IOException {
    SseEmitter emitter = new SseEmitter(0L);
    emitter.send(SseEmitter.event().name("ping").data("ok"));
    emitter.complete();
    return emitter;
  }

  @Operation(summary = "특정 Job의 컨테이너 목록 조회")
  @GetMapping("/{jobId}/containers")
  public ResponseEntity<List<SimpleContainerResponse>> listContainers(@PathVariable UUID jobId) {
    List<SimpleContainerResponse> list = new ArrayList<>();
    list.add(SimpleContainerResponse.builder()
        .id(UUID.randomUUID()).label("container-1").gpuCores(1).cpuCores(2).memoryMb(2048).diskGb(10)
        .build());
    return ResponseEntity.ok(list);
  }

  @Operation(summary = "특정 Job의 컨테이너 상태 실시간 모니터링 (SSE)")
  @GetMapping("/{jobId}/containers/state")
  public SseEmitter monitorContainerState(@PathVariable UUID jobId) throws IOException {
    SseEmitter emitter = new SseEmitter(0L);
    emitter.send(SseEmitter.event().name("state").data("READY"));
    emitter.complete();
    return emitter;
  }

  @Operation(summary = "특정 Job의 컨테이너 메트릭스 실시간 모니터링 (SSE)")
  @GetMapping("/{jobId}/containers/metrics")
  public SseEmitter monitorContainerMetrics(@PathVariable UUID jobId) throws IOException {
    SseEmitter emitter = new SseEmitter(0L);
    emitter.send(SseEmitter.event().name("metrics").data("cpu=10,gpu=0,mem=20,disk=1"));
    emitter.complete();
    return emitter;
  }

  @Operation(summary = "지정한 Job을 수동으로 시작")
  @PostMapping("/{jobId}/start")
  public ResponseEntity<JobResponse> start(@PathVariable UUID jobId) {
    JobResponse res = JobResponse.builder()
        .id(jobId)
        .status("RUNNING")
        .startTime(OffsetDateTime.now())
        .build();
    return ResponseEntity.ok(res);
  }

  @Operation(summary = "실행 중인 Job을 수동으로 중지")
  @PostMapping("/{jobId}/stop")
  public ResponseEntity<JobResponse> stop(@PathVariable UUID jobId) {
    JobResponse res = JobResponse.builder()
        .id(jobId)
        .status("STOPPED")
        .endTime(OffsetDateTime.now())
        .build();
    return ResponseEntity.ok(res);
  }
}
