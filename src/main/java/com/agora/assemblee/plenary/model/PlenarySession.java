package com.agora.assemblee.plenary.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.AssemblySession;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class PlenarySession extends BaseEntity {
    @Column(nullable = false, length = 180)
    private String title;
    private String sessionType;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AssemblySession assemblySession;
    @ManyToOne(fetch = FetchType.LAZY)
    private User presidingUser;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer expectedMembersCount;
    private Integer presentMembersCount;
    private Boolean quorumReached = Boolean.FALSE;
}
