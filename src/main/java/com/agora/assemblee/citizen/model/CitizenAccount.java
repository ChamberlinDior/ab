package com.agora.assemblee.citizen.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CitizenAccount extends BaseEntity {
    @Column(nullable = false, unique = true, length = 180)
    private String email;
    @Column(nullable = false, length = 150)
    private String fullName;
    private Boolean verified = Boolean.FALSE;
}
