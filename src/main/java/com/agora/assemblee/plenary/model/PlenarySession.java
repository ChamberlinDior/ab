package com.agora.assemblee.plenary.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.enums.PlenarySessionStatus;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.AssemblySession;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "plenary_sessions")
public class PlenarySession extends BaseEntity {

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 80)
    private String sessionType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "assembly_session_id", nullable = false)
    private AssemblySession assemblySession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presiding_user_id")
    private User presidingUser;

    @Column(length = 150)
    private String location;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "plenary_status", nullable = false, length = 40)
    private PlenarySessionStatus plenaryStatus = PlenarySessionStatus.DRAFT;

    @Column(nullable = false)
    private Integer expectedMembersCount = 0;

    @Column(nullable = false)
    private Integer presentMembersCount = 0;

    @Column(nullable = false)
    private Integer quorumThresholdPercent = 50;

    @Column(nullable = false)
    private Boolean quorumReached = Boolean.FALSE;

    @Column(columnDefinition = "LONGTEXT")
    private String closingSummary;
}