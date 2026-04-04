package com.agora.assemblee.documents.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.documents.model.DocumentVersion;
import com.agora.assemblee.documents.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @PostMapping(value = "/{documentId}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DocumentVersion> uploadVersion(
            @PathVariable Long documentId,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.ok(
                service.uploadVersion(documentId, file),
                "Version téléversée"
        );
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadCurrentVersion(@PathVariable Long documentId) {
        Resource resource = service.downloadCurrentVersion(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document\"")
                .body(resource);
    }
}