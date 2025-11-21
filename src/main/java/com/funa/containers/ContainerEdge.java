package com.funa.containers;

import com.funa.jobs.Job;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tb_container_edge")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContainerEdge {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_id", nullable = false)
  private Job job;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "source_container_id", nullable = false)
  private Container sourceContainer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "target_container_id", nullable = false)
  private Container targetContainer;

  @Column(name = "edge_key", length = 255, nullable = false)
  private String edgeKey;

  @Column(name = "label", length = 255)
  private String label;

  @Lob
  @Column(name = "condition")
  private String condition; // JSON string

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

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
    if (this.isActive == null) this.isActive = Boolean.TRUE;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = OffsetDateTime.now();
  }
}
