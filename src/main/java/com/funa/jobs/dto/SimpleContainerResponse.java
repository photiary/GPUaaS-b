package com.funa.jobs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleContainerResponse {
  UUID id;
  String label;
  Integer gpuCores;
  Integer cpuCores;
  Integer memoryMb;
  Integer diskGb;
}
