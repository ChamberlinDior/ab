package com.agora.assemblee.documents.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class DocumentVersionResponse {
    private Long id;
    private Integer versionNumber;
    private String originalFilename;
    private Long fileSize;
    private String contentType;
    private String checksum;
    private Boolean currentVersion;
    private Boolean publishedVersion;
    private Boolean archivedVersion;
    private Boolean locked;
    private String uploadedBy;
    private Instant createdAt;
}