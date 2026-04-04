package com.agora.assemblee.documents.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.documents.model.DocumentAccessLog;

import java.util.List;

public interface DocumentAccessLogRepository extends BaseRepository<DocumentAccessLog> {
    default List<DocumentAccessLog> listAll() { return findAll(); }
}
