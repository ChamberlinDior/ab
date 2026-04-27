package com.agora.assemblee.documents.service;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.OwnerType;
import com.agora.assemblee.common.exception.ResourceNotFoundException;
import com.agora.assemblee.documents.dto.DocumentAccessLogResponse;
import com.agora.assemblee.documents.dto.DocumentCreateRequest;
import com.agora.assemblee.documents.dto.DocumentResponse;
import com.agora.assemblee.documents.dto.DocumentSearchRequest;
import com.agora.assemblee.documents.dto.DocumentVersionResponse;
import com.agora.assemblee.documents.enums.DocumentAccessAction;
import com.agora.assemblee.documents.enums.DocumentApprovalDecision;
import com.agora.assemblee.documents.enums.DocumentLifecycleStatus;
import com.agora.assemblee.documents.model.Document;
import com.agora.assemblee.documents.model.DocumentAccessLog;
import com.agora.assemblee.documents.model.DocumentApprovalStep;
import com.agora.assemblee.documents.model.DocumentVersion;
import com.agora.assemblee.documents.repository.DocumentAccessLogRepository;
import com.agora.assemblee.documents.repository.DocumentApprovalStepRepository;
import com.agora.assemblee.documents.repository.DocumentRepository;
import com.agora.assemblee.documents.repository.DocumentVersionRepository;
import com.agora.assemblee.documents.specification.DocumentSpecification;
import com.agora.assemblee.storage.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final DocumentAccessLogRepository accessLogRepository;
    private final DocumentApprovalStepRepository approvalStepRepository;
    private final StorageService storageService;

    @Transactional
    public DocumentResponse create(DocumentCreateRequest request, MultipartFile file, HttpServletRequest httpRequest) {
        Document document = new Document();
        document.setTitle(safeTrim(request.getTitle()));
        document.setDocumentType(safeTrim(request.getDocumentType()));
        document.setReferenceNumber(generateReferenceNumber(request));
        document.setSummary(resolveSummary(request));
        document.setDescription(resolveDescription(request));
        document.setLegalStatus(safeTrim(request.getLegalStatus()));
        document.setClassificationLevel(request.getClassificationLevel());
        document.setOwnerType(request.getOwnerType());
        document.setOwnerId(request.getOwnerId());
        document.setRetentionUntil(request.getRetentionUntil());
        document.setLifecycleStatus(DocumentLifecycleStatus.DRAFT);
        document.setPublished(Boolean.FALSE);
        document.setArchived(Boolean.FALSE);

        Document savedDocument = documentRepository.save(document);

        DocumentVersion version = null;
        boolean hasInitialFile = file != null && !file.isEmpty();

        if (hasInitialFile) {
            version = createVersion(savedDocument, file);
            savedDocument.setCurrentVersionNumber(version.getVersionNumber());
            savedDocument.setCurrentChecksum(version.getChecksum());
            documentRepository.save(savedDocument);

            logAccess(
                    savedDocument,
                    version,
                    DocumentAccessAction.CREATE,
                    httpRequest,
                    "Création du document avec version initiale"
            );
        } else {
            logAccess(
                    savedDocument,
                    null,
                    DocumentAccessAction.CREATE,
                    httpRequest,
                    "Création du document généré sans fichier initial"
            );
        }

        return toResponse(savedDocument, true);
    }

    @Transactional
    public DocumentVersionResponse uploadVersion(Long documentId, MultipartFile file, HttpServletRequest httpRequest) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier de version est obligatoire");
        }

        Document document = getDocumentEntity(documentId);

        DocumentVersion current = versionRepository.findByDocumentIdAndCurrentVersionTrue(documentId).orElse(null);
        if (current != null) {
            current.setCurrentVersion(Boolean.FALSE);
            versionRepository.save(current);
        }

        DocumentVersion version = createVersion(document, file);
        document.setCurrentVersionNumber(version.getVersionNumber());
        document.setCurrentChecksum(version.getChecksum());
        document.setLifecycleStatus(DocumentLifecycleStatus.DRAFT);
        documentRepository.save(document);

        logAccess(document, version, DocumentAccessAction.UPLOAD_VERSION, httpRequest, "Téléversement d'une nouvelle version");

        return toVersionResponse(version);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> search(DocumentSearchRequest request, HttpServletRequest httpRequest) {
        Sort sort = Sort.by(
                "asc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                request.getSortBy() == null || request.getSortBy().isBlank() ? "createdAt" : request.getSortBy()
        );

        Pageable pageable = PageRequest.of(
                request.getPage() == null || request.getPage() < 0 ? 0 : request.getPage(),
                request.getSize() == null || request.getSize() <= 0 ? 20 : request.getSize(),
                sort
        );

        Page<Document> page = documentRepository.findAll(DocumentSpecification.build(request), pageable);
        return page.map(document -> toResponse(document, false));
    }

    @Transactional(readOnly = true)
    public DocumentResponse getById(Long documentId) {
        return toResponse(getDocumentEntity(documentId), true);
    }

    @Transactional(readOnly = true)
    public List<DocumentVersionResponse> getVersions(Long documentId) {
        getDocumentEntity(documentId);
        return versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId)
                .stream()
                .map(this::toVersionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentAccessLogResponse> getAccessLogs(Long documentId) {
        getDocumentEntity(documentId);
        return accessLogRepository.findByDocumentIdOrderByCreatedAtDesc(documentId)
                .stream()
                .map(this::toAccessLogResponse)
                .toList();
    }

    @Transactional
    public Resource downloadCurrentVersion(Long documentId, HttpServletRequest httpRequest) {
        DocumentVersion version = versionRepository.findByDocumentIdAndCurrentVersionTrue(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Version courante introuvable"));

        logAccess(version.getDocument(), version, DocumentAccessAction.DOWNLOAD, httpRequest, "Téléchargement de la version courante");
        return storageService.loadAsResource(version.getStoragePath());
    }

    @Transactional
    public Resource downloadVersion(Long documentId, Long versionId, HttpServletRequest httpRequest) {
        DocumentVersion version = versionRepository.findByIdAndDocumentId(versionId, documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Version introuvable"));

        logAccess(version.getDocument(), version, DocumentAccessAction.DOWNLOAD, httpRequest, "Téléchargement d'une version spécifique");
        return storageService.loadAsResource(version.getStoragePath());
    }

    @Transactional
    public Resource previewVersion(Long documentId, Long versionId, HttpServletRequest httpRequest) {
        DocumentVersion version = versionRepository.findByIdAndDocumentId(versionId, documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Version introuvable"));

        logAccess(version.getDocument(), version, DocumentAccessAction.PREVIEW, httpRequest, "Prévisualisation d'une version");
        return storageService.loadAsResource(version.getStoragePath());
    }

    @Transactional
    public DocumentResponse validateVersion(Long documentId, Long versionId, String comment, HttpServletRequest httpRequest) {
        Document document = getDocumentEntity(documentId);
        DocumentVersion version = getDocumentVersion(documentId, versionId);

        createApprovalStep(document, version, DocumentApprovalDecision.VALIDATED, comment);
        document.setLifecycleStatus(DocumentLifecycleStatus.VALIDATED);
        documentRepository.save(document);

        logAccess(document, version, DocumentAccessAction.VALIDATE_VERSION, httpRequest, comment);

        return toResponse(document, true);
    }

    @Transactional
    public DocumentResponse rejectVersion(Long documentId, Long versionId, String comment, HttpServletRequest httpRequest) {
        Document document = getDocumentEntity(documentId);
        DocumentVersion version = getDocumentVersion(documentId, versionId);

        createApprovalStep(document, version, DocumentApprovalDecision.REJECTED, comment);
        document.setLifecycleStatus(DocumentLifecycleStatus.REJECTED);
        documentRepository.save(document);

        logAccess(document, version, DocumentAccessAction.REJECT_VERSION, httpRequest, comment);

        return toResponse(document, true);
    }

    @Transactional
    public DocumentResponse publishVersion(Long documentId, Long versionId, String comment, HttpServletRequest httpRequest) {
        Document document = getDocumentEntity(documentId);
        DocumentVersion target = getDocumentVersion(documentId, versionId);

        List<DocumentVersion> versions = versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId);
        for (DocumentVersion version : versions) {
            if (Boolean.TRUE.equals(version.getPublishedVersion())) {
                version.setPublishedVersion(Boolean.FALSE);
                versionRepository.save(version);
            }
        }

        target.setPublishedVersion(Boolean.TRUE);
        versionRepository.save(target);

        document.setPublished(Boolean.TRUE);
        document.setPublishedVersionNumber(target.getVersionNumber());
        document.setPublishedAt(Instant.now());
        document.setLifecycleStatus(DocumentLifecycleStatus.PUBLISHED);
        documentRepository.save(document);

        createApprovalStep(document, target, DocumentApprovalDecision.PUBLISHED, comment);
        logAccess(document, target, DocumentAccessAction.PUBLISH_VERSION, httpRequest, comment);

        return toResponse(document, true);
    }

    @Transactional
    public DocumentResponse lockVersion(Long documentId, Long versionId, String comment, HttpServletRequest httpRequest) {
        Document document = getDocumentEntity(documentId);
        DocumentVersion version = getDocumentVersion(documentId, versionId);

        version.setLocked(Boolean.TRUE);
        versionRepository.save(version);

        document.setLockedAt(Instant.now());
        documentRepository.save(document);

        createApprovalStep(document, version, DocumentApprovalDecision.LOCKED, comment);
        logAccess(document, version, DocumentAccessAction.LOCK_VERSION, httpRequest, comment);

        return toResponse(document, true);
    }

    @Transactional
    public DocumentResponse archive(Long documentId, HttpServletRequest httpRequest) {
        Document document = getDocumentEntity(documentId);
        List<DocumentVersion> versions = versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId);

        for (DocumentVersion version : versions) {
            version.setArchivedVersion(Boolean.TRUE);
            versionRepository.save(version);
        }

        document.setArchived(Boolean.TRUE);
        document.setArchivedAt(Instant.now());
        document.setLifecycleStatus(DocumentLifecycleStatus.ARCHIVED);
        documentRepository.save(document);

        DocumentVersion current = versionRepository.findByDocumentIdAndCurrentVersionTrue(documentId).orElse(null);
        if (current != null) {
            createApprovalStep(document, current, DocumentApprovalDecision.ARCHIVED, "Archivage du document");
        }

        logAccess(document, current, DocumentAccessAction.ARCHIVE_DOCUMENT, httpRequest, "Archivage du document");

        return toResponse(document, true);
    }

    private Document getDocumentEntity(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable"));
    }

    private DocumentVersion getDocumentVersion(Long documentId, Long versionId) {
        return versionRepository.findByIdAndDocumentId(versionId, documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Version du document introuvable"));
    }

    private DocumentVersion createVersion(Document document, MultipartFile file) {
        int nextVersion = (int) versionRepository.countByDocumentId(document.getId()) + 1;
        String path = storageService.store(file, "documents/" + document.getId());

        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setVersionNumber(nextVersion);
        version.setOriginalFilename(resolveOriginalFilename(file));
        version.setStoragePath(path);
        version.setContentType(resolveContentType(file));
        version.setFileSize(file.getSize());
        version.setChecksum(calculateChecksum(file));
        version.setCurrentVersion(Boolean.TRUE);
        version.setUploadedBy(resolveCurrentUser());

        return versionRepository.save(version);
    }

    private void createApprovalStep(Document document, DocumentVersion version, DocumentApprovalDecision decision, String comment) {
        DocumentApprovalStep step = new DocumentApprovalStep();
        step.setDocument(document);
        step.setDocumentVersion(version);
        step.setDecision(decision);
        step.setComment(comment);
        step.setDecidedBy(resolveCurrentUser());
        approvalStepRepository.save(step);
    }

    private void logAccess(Document document, DocumentVersion version, DocumentAccessAction action, HttpServletRequest request, String details) {
        DocumentAccessLog log = new DocumentAccessLog();
        log.setDocument(document);
        log.setDocumentVersion(version);
        log.setUser(resolveCurrentUser());
        log.setActionType(action);
        log.setIpAddress(extractClientIp(request));
        log.setDetails(details);
        accessLogRepository.save(log);
    }

    private String generateReferenceNumber(DocumentCreateRequest request) {
        if (request.getReferenceNumber() != null && !request.getReferenceNumber().isBlank()) {
            return request.getReferenceNumber().trim();
        }
        return "DOC-" + System.currentTimeMillis();
    }

    private String calculateChecksum(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de calculer l'empreinte du fichier", e);
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }
        return null;
    }

    private DocumentResponse toResponse(Document document, boolean includeVersions) {
        List<DocumentVersionResponse> versions = includeVersions
                ? versionRepository.findByDocumentIdOrderByVersionNumberDesc(document.getId())
                .stream()
                .map(this::toVersionResponse)
                .toList()
                : List.of();

        boolean hasInitialVersion = document.getCurrentVersionNumber() != null;
        String previewSummary = resolvePreviewSummary(document);
        String previewHtml = buildPreviewHtml(document, previewSummary);

        return DocumentResponse.builder()
                .id(document.getId())
                .referenceNumber(document.getReferenceNumber())
                .title(document.getTitle())
                .documentType(document.getDocumentType())
                .summary(document.getSummary())
                .description(document.getDescription())
                .legalStatus(document.getLegalStatus())
                .classificationLevel(document.getClassificationLevel())
                .ownerType(document.getOwnerType())
                .ownerId(document.getOwnerId())
                .lifecycleStatus(document.getLifecycleStatus())
                .published(document.getPublished())
                .archived(document.getArchived())
                .currentVersionNumber(document.getCurrentVersionNumber())
                .publishedVersionNumber(document.getPublishedVersionNumber())
                .currentChecksum(document.getCurrentChecksum())
                .retentionUntil(document.getRetentionUntil())
                .publishedAt(document.getPublishedAt())
                .archivedAt(document.getArchivedAt())
                .lockedAt(document.getLockedAt())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .versions(versions)
                .generatedDocument(Boolean.TRUE)
                .hasInitialVersion(hasInitialVersion)
                .previewSummary(previewSummary)
                .previewHtml(previewHtml)
                .creationMode(hasInitialVersion ? "WITH_INITIAL_FILE" : "GENERATED_WITHOUT_INITIAL_FILE")
                .build();
    }

    private DocumentVersionResponse toVersionResponse(DocumentVersion version) {
        return DocumentVersionResponse.builder()
                .id(version.getId())
                .versionNumber(version.getVersionNumber())
                .originalFilename(version.getOriginalFilename())
                .fileSize(version.getFileSize())
                .contentType(version.getContentType())
                .checksum(version.getChecksum())
                .currentVersion(version.getCurrentVersion())
                .publishedVersion(version.getPublishedVersion())
                .archivedVersion(version.getArchivedVersion())
                .locked(version.getLocked())
                .uploadedBy(version.getUploadedBy() != null ? version.getUploadedBy().getUsername() : null)
                .createdAt(version.getCreatedAt())
                .build();
    }

    private DocumentAccessLogResponse toAccessLogResponse(DocumentAccessLog log) {
        return DocumentAccessLogResponse.builder()
                .id(log.getId())
                .actionType(log.getActionType() != null ? log.getActionType().name() : null)
                .username(log.getUser() != null ? log.getUser().getUsername() : null)
                .ipAddress(log.getIpAddress())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private String resolveSummary(DocumentCreateRequest request) {
        if (request.getGeneratedSummary() != null && !request.getGeneratedSummary().isBlank()) {
            return request.getGeneratedSummary().trim();
        }
        if (request.getSummary() != null && !request.getSummary().isBlank()) {
            return request.getSummary().trim();
        }

        String ownerLabel = formatOwnerType(request.getOwnerType());
        String classification = request.getClassificationLevel() != null
                ? formatClassification(request.getClassificationLevel())
                : "Non classé";

        return "Document de type « " + safeTrim(request.getDocumentType())
                + " » rattaché à " + ownerLabel
                + " (#" + request.getOwnerId() + "), classé « " + classification + " ».";
    }

    private String resolveDescription(DocumentCreateRequest request) {
        if (request.getGeneratedBody() != null && !request.getGeneratedBody().isBlank()) {
            return request.getGeneratedBody().trim();
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            return request.getDescription().trim();
        }

        return """
                Document généré automatiquement par la GED parlementaire.
                                
                Ce document a été créé à partir des données métier sélectionnées lors du parcours de création.
                Il peut être enrichi ultérieurement par une version jointe, une validation, une publication ou un archivage.
                """.trim();
    }

    private String resolvePreviewSummary(Document document) {
        if (document.getSummary() != null && !document.getSummary().isBlank()) {
            return document.getSummary();
        }
        return "Document parlementaire généré et enregistré dans la GED.";
    }

    private String buildPreviewHtml(Document document, String previewSummary) {
        String classification = document.getClassificationLevel() != null
                ? formatClassification(document.getClassificationLevel())
                : "—";

        String ownerType = document.getOwnerType() != null
                ? formatOwnerType(document.getOwnerType())
                : "—";

        String legalStatus = document.getLegalStatus() != null && !document.getLegalStatus().isBlank()
                ? document.getLegalStatus()
                : "Non précisé";

        String retention = document.getRetentionUntil() != null
                ? document.getRetentionUntil().toString()
                : "Non définie";

        return """
                <html>
                  <head>
                    <meta charset="UTF-8" />
                    <style>
                      body { font-family: Arial, sans-serif; color: #07111F; background: #FFFFFF; margin: 0; padding: 32px; }
                      .page { max-width: 794px; margin: 0 auto; border: 1px solid #E6EDF8; border-radius: 18px; overflow: hidden; }
                      .top { height: 6px; background: linear-gradient(90deg, #009A44, #C9A84C, #0099CC); }
                      .content { padding: 28px; }
                      .eyebrow { font-size: 12px; font-weight: 700; color: #41516C; letter-spacing: 1px; text-transform: uppercase; }
                      .title { font-size: 24px; font-weight: 800; margin: 10px 0 6px 0; }
                      .subtitle { font-size: 13px; color: #41516C; margin-bottom: 22px; }
                      .block { margin-top: 18px; padding: 16px; border: 1px solid #E6EDF8; border-radius: 12px; background: #F8FAFF; }
                      .label { font-size: 11px; font-weight: 700; color: #41516C; text-transform: uppercase; margin-bottom: 6px; }
                      .value { font-size: 14px; color: #07111F; }
                    </style>
                  </head>
                  <body>
                    <div class="page">
                      <div class="top"></div>
                      <div class="content">
                        <div class="eyebrow">Assemblée Nationale du Gabon · GED Parlementaire</div>
                        <div class="title">%s</div>
                        <div class="subtitle">%s</div>

                        <div class="block">
                          <div class="label">Référence</div>
                          <div class="value">%s</div>
                        </div>

                        <div class="block">
                          <div class="label">Type documentaire</div>
                          <div class="value">%s</div>
                        </div>

                        <div class="block">
                          <div class="label">Classification</div>
                          <div class="value">%s</div>
                        </div>

                        <div class="block">
                          <div class="label">Contexte métier</div>
                          <div class="value">%s · #%s</div>
                        </div>

                        <div class="block">
                          <div class="label">Statut juridique</div>
                          <div class="value">%s</div>
                        </div>

                        <div class="block">
                          <div class="label">Rétention</div>
                          <div class="value">%s</div>
                        </div>

                        <div class="block">
                          <div class="label">Résumé</div>
                          <div class="value">%s</div>
                        </div>

                        <div class="block">
                          <div class="label">Contenu</div>
                          <div class="value">%s</div>
                        </div>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(
                escapeHtml(document.getTitle()),
                escapeHtml("Document institutionnel généré automatiquement"),
                escapeHtml(nullToDash(document.getReferenceNumber())),
                escapeHtml(nullToDash(document.getDocumentType())),
                escapeHtml(classification),
                escapeHtml(ownerType),
                escapeHtml(String.valueOf(document.getOwnerId())),
                escapeHtml(legalStatus),
                escapeHtml(retention),
                escapeHtml(previewSummary),
                escapeHtml(nullToDash(document.getDescription()))
        );
    }

    private String resolveOriginalFilename(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            return "document-" + System.currentTimeMillis();
        }
        return name.trim();
    }

    private String resolveContentType(MultipartFile file) {
        String originalFilename = resolveOriginalFilename(file);
        String contentType = file.getContentType();

        if (contentType != null && !contentType.isBlank() && !"application/octet-stream".equalsIgnoreCase(contentType)) {
            return contentType;
        }

        String lower = originalFilename.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".doc")) return "application/msword";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".xls")) return "application/vnd.ms-excel";
        if (lower.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (lower.endsWith(".ppt")) return "application/vnd.ms-powerpoint";
        if (lower.endsWith(".pptx")) return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        if (lower.endsWith(".txt")) return "text/plain";
        if (lower.endsWith(".csv")) return "text/csv";
        if (lower.endsWith(".json")) return "application/json";
        if (lower.endsWith(".xml")) return "application/xml";
        if (lower.endsWith(".zip")) return "application/zip";
        if (lower.endsWith(".rar")) return "application/x-rar-compressed";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".mp3")) return "audio/mpeg";

        return "application/octet-stream";
    }

    private String formatOwnerType(OwnerType ownerType) {
        if (ownerType == null) return "Contexte non défini";
        return switch (ownerType) {
            case COMMITTEE -> "Commission";
            case DEPUTY -> "Député";
            case PLENARY_SESSION -> "Séance plénière";
            case LEGISLATIVE_TEXT -> "Texte législatif";
            case TEXT_VERSION -> "Version de texte";
            case WORKFLOW_INSTANCE -> "Workflow";
            case INTERNAL_MESSAGE -> "Message interne";
            case CITIZEN_CONSULTATION -> "Consultation citoyenne";
            case FINANCE_RECORD -> "Document financier";
        };
    }

    private String formatClassification(DocumentClassificationLevel level) {
        if (level == null) return "Non définie";
        return switch (level) {
            case PUBLIC_OPEN -> "Public ouvert";
            case PUBLIC_CONTROLLED -> "Public contrôlé";
            case INTERNAL -> "Interne";
            case COMMISSION -> "Commission";
            case BUREAU -> "Bureau";
            case PRESIDENCE -> "Présidence";
        };
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private String nullToDash(String value) {
        return (value == null || value.isBlank()) ? "—" : value;
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}