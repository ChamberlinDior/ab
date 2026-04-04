package com.agora.assemblee.common.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 80)
    private String reference;

    @Column(nullable = false, length = 50)
    private String status = "ACTIVE";

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(length = 150)
    private String createdBy;

    @LastModifiedBy
    @Column(length = 150)
    private String updatedBy;

    private Instant deletedAt;

    @Version
    private Long version;

    @PrePersist
    public void ensureDefaults() {
        if (reference == null || reference.isBlank()) {
            reference = ReferenceGenerator.generate(getClass().getSimpleName());
        }
        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }
    }
}
