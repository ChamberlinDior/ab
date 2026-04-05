package com.agora.assemblee.plenary.model;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.enums.AttendanceStatus;
import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.Deputy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "session_attendances",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_session_attendance_session_deputy", columnNames = {"plenary_session_id", "deputy_id"})
        }
)
public class SessionAttendance extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "plenary_session_id", nullable = false)
    private PlenarySession plenarySession;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deputy_id", nullable = false)
    private Deputy deputy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AttendanceStatus attendanceStatus = AttendanceStatus.PRESENT;

    @Column(nullable = false)
    private Boolean present = Boolean.TRUE;

    @Column(length = 1000)
    private String absenceJustification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id")
    private User recordedBy;
}