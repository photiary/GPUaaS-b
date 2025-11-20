package com.funa.agents.metrics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricsData {
    String containerId;
    Instant timestamp;
    double cpuUsage;
    double gpuUsage;
    double memoryUsage;

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
