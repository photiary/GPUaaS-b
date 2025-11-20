package com.funa.jobs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobRequest {
  @NotBlank String name;
  String description;

  @Min(0)
  Integer requestedGpus;

  @Min(0)
  Integer requestedCpus;

  @Min(0)
  Integer requestedMemory; // MB

  String metadata; // JSON string for simplicity
}
