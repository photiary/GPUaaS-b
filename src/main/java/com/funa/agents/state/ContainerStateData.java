package com.funa.agents.state;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerStateData {
    String containerId;
    String state; // e.g., RUNNING, STOPPED, FAILED
    Instant timestamp;
    Instant startTime;
    Instant endTime;

    @JsonProperty("timestamp")
    public String getTimestampIso() {
        return timestamp != null ? timestamp.toString() : null;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            return this.toString();
        }
    }
}
