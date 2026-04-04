package com.agora.assemblee.institution.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ParliamentaryGroup extends BaseEntity {

    @Column(nullable = false, unique = true, length = 160)
    private String name;

    @Column(length = 40)
    private String acronym;

    @Column(length = 1200)
    private String description;

    @Column(length = 160)
    private String leaderName;

    @Column(length = 160)
    private String contactEmail;

    @Column(length = 80)
    private String contactPhone;

    @Column(nullable = false)
    private Boolean official = Boolean.TRUE;
}