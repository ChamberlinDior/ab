package com.agora.assemblee.communication.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Notification extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User recipient;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(length = 1000)
    private String body;
    private Boolean readFlag = Boolean.FALSE;
}
