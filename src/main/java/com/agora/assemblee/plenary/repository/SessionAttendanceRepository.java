package com.agora.assemblee.plenary.repository;

import com.agora.assemblee.common.enums.AttendanceStatus;
import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.plenary.model.SessionAttendance;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SessionAttendanceRepository extends BaseRepository<SessionAttendance> {

    List<SessionAttendance> findByPlenarySessionIdOrderByDeputyIdAsc(Long plenarySessionId);

    Optional<SessionAttendance> findByPlenarySessionIdAndDeputyId(Long plenarySessionId, Long deputyId);

    long countByPlenarySessionIdAndAttendanceStatusIn(Long plenarySessionId, Collection<AttendanceStatus> statuses);
}