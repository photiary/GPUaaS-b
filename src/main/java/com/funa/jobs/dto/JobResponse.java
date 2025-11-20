package com.funa.jobs.dto;

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
public class JobResponse {
  UUID id;
  String name;
  String description;
  String status; // QUEUED, RUNNING, COMPLETED, FAILED, STOPPED
  OffsetDateTime submitTime;
  OffsetDateTime startTime;
  OffsetDateTime endTime;
  Integer requestedGpus;
  Integer requestedCpus;
  Integer requestedMemory;
  String metadata;
}
