package com.agora.assemblee.communication.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class InternalMessage extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User sender;
    @Column(nullable = false, length = 250)
    private String subject;
    @Column(columnDefinition = "LONGTEXT")
    private String body;
    @Column(length = 50)
    private String channelType;
}
