package com.funa.nodes;

import com.funa.nodes.dto.NodeRequest;
import com.funa.nodes.dto.NodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nodes")
@Tag(name = "node", description = "리소스 노드 관리 API")
public class NodeController {

  private final NodeService nodeService;

  public NodeController(NodeService nodeService) {
    this.nodeService = nodeService;
  }

  @Operation(summary = "리소스 노드 등록")
  @PostMapping
  public ResponseEntity<NodeResponse> create(@Validated @RequestBody NodeRequest req) {
    return ResponseEntity.ok(nodeService.create(req));
  }

  @Operation(summary = "리소스 노드 목록 조회")
  @GetMapping
  public ResponseEntity<List<NodeResponse>> list() {
    return ResponseEntity.ok(nodeService.findAll());
  }

  @Operation(summary = "리소스 노드 상세 조회")
  @GetMapping("/{nodeId}")
  public ResponseEntity<NodeResponse> get(@PathVariable UUID nodeId) {
    return nodeService.findById(nodeId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "리소스 노드 수정")
  @PutMapping("/{nodeId}")
  public ResponseEntity<NodeResponse> update(@PathVariable UUID nodeId, @Validated @RequestBody NodeRequest req) {
    return nodeService.update(nodeId, req)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "리소스 노드 삭제")
  @DeleteMapping("/{nodeId}")
  public ResponseEntity<Void> delete(@PathVariable UUID nodeId) {
    nodeService.delete(nodeId);
    return ResponseEntity.noContent().build();
  }
}
