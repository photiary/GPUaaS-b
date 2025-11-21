package com.funa.containers.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

/**
 * 컨테이너 엣지 생성/수정 요청 DTO
 */
@Value
@Builder
public class ContainerEdgeRequest {
  UUID sourceContainerId;
  UUID targetContainerId;
  String edgeKey;
  String label;
  String condition; // JSON string
  Boolean isActive;
}
