package com.agora.assemblee.legislation.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.legislation.model.CommissionReport;

import java.util.List;

public interface CommissionReportRepository extends BaseRepository<CommissionReport> {

    List<CommissionReport> findByLegislativeTextIdOrderByCreatedAtDesc(Long legislativeTextId);

    long countByLegislativeTextId(Long legislativeTextId);
}