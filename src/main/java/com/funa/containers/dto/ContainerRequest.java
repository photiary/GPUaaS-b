package com.funa.containers.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerRequest {
  @NotBlank String label;
  String description;
  Integer sequence;
  String status; // CREATED, WAITING, RUNNING, COMPLETED, FAILED, STOPPED
  String config; // JSON string
  Double positionX;
  Double positionY;
  // 관계 식별자 (옵션)
  UUID jobId;
  UUID nodeId;
}
