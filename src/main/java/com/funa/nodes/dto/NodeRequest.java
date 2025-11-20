package com.funa.nodes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeRequest {
  @NotBlank String name;
  @NotBlank String type; // GPU or CPU
  Integer gpuCount;
  Integer cpuCores;
  Integer memoryCapacity; // MB
  String status; // ONLINE, OFFLINE, MAINTENANCE
}
