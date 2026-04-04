package com.agora.assemblee.auth.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class UserSession extends BaseEntity {
    @ManyToOne(optional = false)
    private User user;
    private String refreshToken;
    private String deviceName;
    private String ipAddress;
    private Instant expiresAt;
    private Instant revokedAt;
}
