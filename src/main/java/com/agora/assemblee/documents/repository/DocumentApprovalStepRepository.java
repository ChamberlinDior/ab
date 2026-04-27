package com.agora.assemblee.documents.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.documents.model.DocumentApprovalStep;

import java.util.List;

public interface DocumentApprovalStepRepository extends BaseRepository<DocumentApprovalStep> {
    List<DocumentApprovalStep> findByDocumentIdOrderByCreatedAtDesc(Long documentId);
}