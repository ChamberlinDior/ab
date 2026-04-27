package com.agora.assemblee.documents.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class DocumentAccessLogResponse {
    private Long id;
    private String actionType;
    private String username;
    private String ipAddress;
    private String details;
    private Instant createdAt;
}