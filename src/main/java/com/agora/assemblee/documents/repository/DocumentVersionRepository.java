package com.agora.assemblee.documents.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.documents.model.DocumentVersion;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository extends BaseRepository<DocumentVersion> {

    List<DocumentVersion> findByDocumentIdOrderByVersionNumberDesc(Long documentId);

    Optional<DocumentVersion> findByDocumentIdAndCurrentVersionTrue(Long documentId);

    Optional<DocumentVersion> findByDocumentIdAndVersionNumber(Long documentId, Integer versionNumber);

    Optional<DocumentVersion> findByIdAndDocumentId(Long versionId, Long documentId);

    long countByDocumentId(Long documentId);
}