package com.agora.assemblee.documents.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DocumentAccessLog extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Document document;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false, length = 50)
    private String actionType;
    private String ipAddress;
}
