package com.agora.assemblee.documents.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.documents.model.DocumentVersion;

import java.util.List;

public interface DocumentVersionRepository extends BaseRepository<DocumentVersion> {
    default List<DocumentVersion> listAll() { return findAll(); }
}
