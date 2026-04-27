package com.agora.assemblee.documents.dto;

import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.OwnerType;
import com.agora.assemblee.documents.enums.DocumentLifecycleStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentSearchRequest {
    private String keyword;
    private String documentType;
    private DocumentClassificationLevel classificationLevel;
    private OwnerType ownerType;
    private Long ownerId;
    private Boolean published;
    private Boolean archived;
    private DocumentLifecycleStatus status;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
}