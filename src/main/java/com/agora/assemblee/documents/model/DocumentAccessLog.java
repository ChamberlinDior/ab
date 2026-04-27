package com.agora.assemblee.documents.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.documents.enums.DocumentAccessAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "document_access_logs")
public class DocumentAccessLog extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_version_id")
    private DocumentVersion documentVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DocumentAccessAction actionType;

    @Column(length = 100)
    private String ipAddress;

    @Column(length = 1000)
    private String details;
}