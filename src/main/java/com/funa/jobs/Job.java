package com.funa.jobs;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tb_job")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Job {

  public enum Status { QUEUED, RUNNING, COMPLETED, FAILED, STOPPED }

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "description")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private Status status;

  @Column(name = "submit_time")
  private OffsetDateTime submitTime;

  @Column(name = "start_time")
  private OffsetDateTime startTime;

  @Column(name = "end_time")
  private OffsetDateTime endTime;

  @Column(name = "requested_gpus")
  private Integer requestedGpus;

  @Column(name = "requested_cpus")
  private Integer requestedCpus;

  @Column(name = "requested_memory")
  private Integer requestedMemory;

  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "metadata")
  private String metadata; // JSON string

  // audit fields
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
    if (this.status == null) this.status = Status.STOPPED;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = OffsetDateTime.now();
  }
}
