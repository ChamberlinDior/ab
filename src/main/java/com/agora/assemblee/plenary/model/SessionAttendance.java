package com.agora.assemblee.plenary.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.Deputy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SessionAttendance extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PlenarySession plenarySession;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Deputy deputy;
    private Boolean present = Boolean.TRUE;
    private String absenceJustification;
    @ManyToOne(fetch = FetchType.LAZY)
    private User recordedBy;
}
