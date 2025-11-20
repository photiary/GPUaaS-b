package com.funa.agents.state;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobStateData {
    String jobId;
    @Singular
    List<ContainerStateData> containers;
    Instant timestamp;

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            return this.toString();
        }
    }
}
