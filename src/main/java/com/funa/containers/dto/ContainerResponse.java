package com.funa.containers.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerResponse {
  UUID id;
  String label;
  String description;
  Integer sequence;
  String status;
  String config;
  Double positionX;
  Double positionY;

  public static ContainerResponse from(ContainerRequest req, UUID id) {
    return ContainerResponse.builder()
        .id(id)
        .label(req.getLabel())
        .description(req.getDescription())
        .sequence(req.getSequence())
        .status(req.getStatus())
        .config(req.getConfig())
        .positionX(req.getPositionX())
        .positionY(req.getPositionY())
        .build();
  }
}
