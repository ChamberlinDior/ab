package com.agora.assemblee.institution.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Deputy extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String firstName;

    @Column(nullable = false, length = 120)
    private String lastName;

    @Column(nullable = false, unique = true, length = 80)
    private String deputyNumber;

    @Column(length = 20)
    private String gender;

    @Column(length = 140)
    private String constituency;

    @Column(length = 140)
    private String province;

    @Column(length = 140)
    private String district;

    @Column(length = 140)
    private String politicalParty;

    @Column(length = 60)
    private String mandateStatus = "EN_FONCTION";

    @Column(length = 160)
    private String email;

    @Column(length = 80)
    private String phoneNumber;

    @Column(length = 80)
    private String whatsappNumber;

    @Column(length = 500)
    private String photoUrl;

    @Column(length = 40)
    private String seatNumber;

    @Column(length = 255)
    private String officialAddress;

    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parliamentary_group_id")
    private ParliamentaryGroup parliamentaryGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_session_id")
    private AssemblySession currentSession;
}