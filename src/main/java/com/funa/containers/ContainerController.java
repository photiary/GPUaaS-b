package com.funa.containers;

import com.funa.containers.dto.ContainerRequest;
import com.funa.containers.dto.ContainerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/containers")
@Tag(name = "App Container", description = "컨테이너 관리 API")
public class ContainerController {

  private final ContainerService containerService;

  public ContainerController(ContainerService containerService) {
    this.containerService = containerService;
  }

  @Operation(summary = "컨테이너 생성")
  @PostMapping
  public ResponseEntity<ContainerResponse> create(@Validated @RequestBody ContainerRequest req) {
    return ResponseEntity.ok(containerService.create(req));
  }

  @Operation(summary = "컨테이너 상세 조회")
  @GetMapping("/{containerId}")
  public ResponseEntity<ContainerResponse> get(@PathVariable UUID containerId) {
    return containerService.findById(containerId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "컨테이너 수정")
  @PutMapping("/{containerId}")
  public ResponseEntity<ContainerResponse> update(
      @PathVariable UUID containerId, @Validated @RequestBody ContainerRequest req) {
    return containerService.update(containerId, req)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "컨테이너 삭제")
  @DeleteMapping("/{containerId}")
  public ResponseEntity<Void> delete(@PathVariable UUID containerId) {
    containerService.delete(containerId);
    return ResponseEntity.noContent().build();
  }
}
