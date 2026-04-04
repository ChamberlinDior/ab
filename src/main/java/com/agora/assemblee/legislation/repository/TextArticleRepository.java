package com.agora.assemblee.legislation.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.legislation.model.TextArticle;

import java.util.List;

public interface TextArticleRepository extends BaseRepository<TextArticle> {

    List<TextArticle> findByTextVersionIdOrderBySortOrderAsc(Long textVersionId);

    long countByTextVersionLegislativeTextId(Long legislativeTextId);
}