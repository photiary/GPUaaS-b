package com.funa.nodes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeResponse {
  UUID id;
  String name;
  String type;
  Integer gpuCount;
  Integer cpuCores;
  Integer memoryCapacity;
  String status;
  OffsetDateTime lastHeartbeat;

  public static NodeResponse from(NodeRequest req, UUID id, OffsetDateTime heartbeat) {
    return NodeResponse.builder()
        .id(id)
        .name(req.getName())
        .type(req.getType())
        .gpuCount(req.getGpuCount())
        .cpuCores(req.getCpuCores())
        .memoryCapacity(req.getMemoryCapacity())
        .status(req.getStatus())
        .lastHeartbeat(heartbeat)
        .build();
  }
}
