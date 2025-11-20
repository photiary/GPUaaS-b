package com.funa.containers;

import com.funa.jobs.Job;
import com.funa.nodes.Node;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tb_container")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Container {

  public enum Status { CREATED, WAITING, RUNNING, COMPLETED, FAILED, STOPPED }

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_id", nullable = false)
  private Job job;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "node_id")
  private Node node;

  @Column(name = "label", nullable = false, length = 255)
  private String label;

  @Lob
  @Column(name = "description")
  private String description;

  @Column(name = "sequence_no")
  private Integer sequence;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private Status status;

  @Lob
  @Column(name = "config")
  private String config; // JSON string

  @Column(name = "position_x")
  private Double positionX;

  @Column(name = "position_y")
  private Double positionY;

  @Column(name = "start_time")
  private OffsetDateTime startTime;

  @Column(name = "end_time")
  private OffsetDateTime endTime;

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
    if (this.status == null) this.status = Status.CREATED;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = OffsetDateTime.now();
  }
}
