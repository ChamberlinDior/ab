package com.agora.assemblee.legislation.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.legislation.model.TextAssignment;

import java.util.List;

public interface TextAssignmentRepository extends BaseRepository<TextAssignment> {

    List<TextAssignment> findByLegislativeTextIdOrderByCreatedAtDesc(Long legislativeTextId);

    long countByLegislativeTextId(Long legislativeTextId);
}