package com.agora.assemblee.documents.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.documents.dto.DocumentAccessLogResponse;
import com.agora.assemblee.documents.dto.DocumentApprovalDecisionRequest;
import com.agora.assemblee.documents.dto.DocumentCreateRequest;
import com.agora.assemblee.documents.dto.DocumentResponse;
import com.agora.assemblee.documents.dto.DocumentSearchRequest;
import com.agora.assemblee.documents.dto.DocumentVersionResponse;
import com.agora.assemblee.documents.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<DocumentResponse> createGenerated(
            @RequestBody @Valid DocumentCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(
                service.create(request, null, httpRequest),
                "Document généré et créé avec succès"
        );
    }

    @PostMapping(
            value = "/multipart",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<DocumentResponse> createMultipart(
            @RequestPart("metadata") @Valid DocumentCreateRequest metadata,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request
    ) {
        return ApiResponse.ok(
                service.create(metadata, file, request),
                file != null && !file.isEmpty()
                        ? "Document créé avec fichier joint"
                        : "Document généré avec métadonnées uniquement"
        );
    }

    @PostMapping(value = "/{documentId}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DocumentVersionResponse> uploadVersion(
            @PathVariable Long documentId,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request
    ) {
        return ApiResponse.ok(
                service.uploadVersion(documentId, file, request),
                "Nouvelle version téléversée avec succès"
        );
    }

    @PostMapping("/search")
    public ApiResponse<Page<DocumentResponse>> search(
            @RequestBody(required = false) DocumentSearchRequest request,
            HttpServletRequest httpRequest
    ) {
        DocumentSearchRequest effectiveRequest = request == null ? new DocumentSearchRequest() : request;
        return ApiResponse.ok(service.search(effectiveRequest, httpRequest), "Résultats récupérés");
    }

    @GetMapping("/{documentId}")
    public ApiResponse<DocumentResponse> getById(@PathVariable Long documentId) {
        return ApiResponse.ok(service.getById(documentId), "Document récupéré");
    }

    @GetMapping("/{documentId}/versions")
    public ApiResponse<List<DocumentVersionResponse>> getVersions(@PathVariable Long documentId) {
        return ApiResponse.ok(service.getVersions(documentId), "Versions récupérées");
    }

    @GetMapping("/{documentId}/access-logs")
    public ApiResponse<List<DocumentAccessLogResponse>> getAccessLogs(@PathVariable Long documentId) {
        return ApiResponse.ok(service.getAccessLogs(documentId), "Historique récupéré");
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadCurrentVersion(
            @PathVariable Long documentId,
            HttpServletRequest request
    ) {
        Resource resource = service.downloadCurrentVersion(documentId, request);
        String filename = resource != null ? resource.getFilename() : "document";
        return ResponseEntity.ok()
                .contentType(resolveMediaType(filename))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition("attachment", filename))
                .body(resource);
    }

    @GetMapping("/{documentId}/versions/{versionId}/download")
    public ResponseEntity<Resource> downloadVersion(
            @PathVariable Long documentId,
            @PathVariable Long versionId,
            HttpServletRequest request
    ) {
        Resource resource = service.downloadVersion(documentId, versionId, request);
        String filename = resource != null ? resource.getFilename() : "document";
        return ResponseEntity.ok()
                .contentType(resolveMediaType(filename))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition("attachment", filename))
                .body(resource);
    }

    @GetMapping("/{documentId}/versions/{versionId}/preview")
    public ResponseEntity<Resource> previewVersion(
            @PathVariable Long documentId,
            @PathVariable Long versionId,
            HttpServletRequest request
    ) {
        Resource resource = service.previewVersion(documentId, versionId, request);
        String filename = resource != null ? resource.getFilename() : "document";
        return ResponseEntity.ok()
                .contentType(resolveMediaType(filename))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition("inline", filename))
                .body(resource);
    }

    @PostMapping("/{documentId}/versions/{versionId}/validate")
    public ApiResponse<DocumentResponse> validateVersion(
            @PathVariable Long documentId,
            @PathVariable Long versionId,
            @RequestBody @Valid DocumentApprovalDecisionRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(service.validateVersion(documentId, versionId, request.getComment(), httpRequest), "Version validée");
    }

    @PostMapping("/{documentId}/versions/{versionId}/reject")
    public ApiResponse<DocumentResponse> rejectVersion(
            @PathVariable Long documentId,
            @PathVariable Long versionId,
            @RequestBody @Valid DocumentApprovalDecisionRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(service.rejectVersion(documentId, versionId, request.getComment(), httpRequest), "Version rejetée");
    }

    @PostMapping("/{documentId}/versions/{versionId}/publish")
    public ApiResponse<DocumentResponse> publishVersion(
            @PathVariable Long documentId,
            @PathVariable Long versionId,
            @RequestBody @Valid DocumentApprovalDecisionRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(service.publishVersion(documentId, versionId, request.getComment(), httpRequest), "Version publiée");
    }

    @PostMapping("/{documentId}/versions/{versionId}/lock")
    public ApiResponse<DocumentResponse> lockVersion(
            @PathVariable Long documentId,
            @PathVariable Long versionId,
            @RequestBody @Valid DocumentApprovalDecisionRequest request,
            HttpServletRequest httpRequest
    ) {
        return ApiResponse.ok(service.lockVersion(documentId, versionId, request.getComment(), httpRequest), "Version verrouillée");
    }

    @PostMapping("/{documentId}/archive")
    public ApiResponse<DocumentResponse> archive(
            @PathVariable Long documentId,
            HttpServletRequest request
    ) {
        return ApiResponse.ok(service.archive(documentId, request), "Document archivé");
    }

    private MediaType resolveMediaType(String filename) {
        if (!StringUtils.hasText(filename)) return MediaType.APPLICATION_OCTET_STREAM;
        String lower = filename.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return MediaType.parseMediaType("image/webp");
        if (lower.endsWith(".svg")) return MediaType.parseMediaType("image/svg+xml");
        if (lower.endsWith(".txt")) return MediaType.TEXT_PLAIN;
        if (lower.endsWith(".csv")) return MediaType.parseMediaType("text/csv");
        if (lower.endsWith(".json")) return MediaType.APPLICATION_JSON;
        if (lower.endsWith(".xml")) return MediaType.APPLICATION_XML;
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return MediaType.TEXT_HTML;
        if (lower.endsWith(".doc")) return MediaType.parseMediaType("application/msword");
        if (lower.endsWith(".docx")) return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        if (lower.endsWith(".xls")) return MediaType.parseMediaType("application/vnd.ms-excel");
        if (lower.endsWith(".xlsx")) return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        if (lower.endsWith(".ppt")) return MediaType.parseMediaType("application/vnd.ms-powerpoint");
        if (lower.endsWith(".pptx")) return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        if (lower.endsWith(".zip")) return MediaType.parseMediaType("application/zip");
        if (lower.endsWith(".rar")) return MediaType.parseMediaType("application/x-rar-compressed");
        if (lower.endsWith(".7z")) return MediaType.parseMediaType("application/x-7z-compressed");
        if (lower.endsWith(".mp4")) return MediaType.parseMediaType("video/mp4");
        if (lower.endsWith(".mp3")) return MediaType.parseMediaType("audio/mpeg");

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private String contentDisposition(String type, String filename) {
        String safeFilename = StringUtils.hasText(filename) ? filename : "document";
        String encoded = URLEncoder.encode(safeFilename, StandardCharsets.UTF_8).replace("+", "%20");
        return type + "; filename=\"" + safeFilename + "\"; filename*=UTF-8''" + encoded;
    }
}