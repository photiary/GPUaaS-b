package com.funa.nodes;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tb_node")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Node {

  public enum Type { GPU, CPU }
  public enum Status { ONLINE, OFFLINE, MAINTENANCE }

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 10)
  private Type type;

  @Column(name = "gpu_count")
  private Integer gpuCount;

  @Column(name = "cpu_cores")
  private Integer cpuCores;

  @Column(name = "memory_capacity")
  private Integer memoryCapacity; // MB

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 15)
  private Status status;

  @Column(name = "last_heartbeat")
  private OffsetDateTime lastHeartbeat;

  // audit
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "created_id")
  private String createdId;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  @Column(name = "updated_id")
  private String updatedId;

  @PrePersist
  protected void onCreate() {
    this.createdAt = OffsetDateTime.now();
    this.updatedAt = this.createdAt;
    if (this.status == null) this.status = Status.ONLINE;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = OffsetDateTime.now();
  }
}
