package com.agora.assemblee.documents.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.documents.model.Document;

import java.util.List;

public interface DocumentRepository extends BaseRepository<Document> {
    default List<Document> listAll() { return findAll(); }
}
