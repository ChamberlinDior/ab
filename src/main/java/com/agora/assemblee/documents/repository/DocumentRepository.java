package com.agora.assemblee.documents.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.documents.model.Document;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DocumentRepository extends BaseRepository<Document>, JpaSpecificationExecutor<Document> {
    Optional<Document> findByReferenceNumber(String referenceNumber);
}