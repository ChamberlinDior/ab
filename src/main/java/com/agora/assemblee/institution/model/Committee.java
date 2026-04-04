package com.agora.assemblee.institution.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Committee extends BaseEntity {

    @Column(nullable = false, unique = true, length = 180)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(length = 80)
    private String committeeType;

    @Column(length = 180)
    private String thematicScope;

    @Column(length = 1200)
    private String description;

    @Column(length = 120)
    private String room;

    @Column(length = 160)
    private String contactEmail;

    @Column(length = 80)
    private String contactPhone;

    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_session_id")
    private AssemblySession activeSession;
}