package com.agora.assemblee.audit.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AuditEvent extends BaseEntity {
    @Column(nullable = false, length = 120)
    private String eventType;
    private String actor;
    private String targetType;
    private String targetReference;
    @Column(columnDefinition = "LONGTEXT")
    private String details;
}
