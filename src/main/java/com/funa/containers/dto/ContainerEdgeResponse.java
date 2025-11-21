package com.funa.containers.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

/**
 * 컨테이너 엣지 응답 DTO
 */
@Value
@Builder
public class ContainerEdgeResponse {
  UUID id;
  UUID jobId;
  UUID sourceContainerId;
  UUID targetContainerId;
  String edgeKey;
  String label;
  String condition;
  Boolean isActive;
  OffsetDateTime createdAt;
  OffsetDateTime updatedAt;
}
