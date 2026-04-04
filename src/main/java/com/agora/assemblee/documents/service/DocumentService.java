package com.agora.assemblee.documents.service;

import com.agora.assemblee.common.exception.ResourceNotFoundException;
import com.agora.assemblee.documents.model.Document;
import com.agora.assemblee.documents.model.DocumentAccessLog;
import com.agora.assemblee.documents.model.DocumentVersion;
import com.agora.assemblee.documents.repository.DocumentAccessLogRepository;
import com.agora.assemblee.documents.repository.DocumentRepository;
import com.agora.assemblee.documents.repository.DocumentVersionRepository;
import com.agora.assemblee.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final DocumentAccessLogRepository accessLogRepository;
    private final StorageService storageService;

    @Transactional
    public DocumentVersion uploadVersion(Long documentId, MultipartFile file) {
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Document introuvable"));
        String path = storageService.store(file, "documents");
        versionRepository.findAll().stream().filter(v -> v.getDocument().getId().equals(documentId) && Boolean.TRUE.equals(v.getCurrentVersion())).forEach(v -> { v.setCurrentVersion(Boolean.FALSE); versionRepository.save(v); });
        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setVersionNumber((int) versionRepository.findAll().stream().filter(v -> v.getDocument().getId().equals(documentId)).count() + 1);
        version.setOriginalFilename(file.getOriginalFilename());
        version.setStoragePath(path);
        version.setContentType(file.getContentType());
        version.setFileSize(file.getSize());
        return versionRepository.save(version);
    }

    @Transactional
    public Resource downloadCurrentVersion(Long documentId) {
        DocumentVersion version = versionRepository.findAll().stream()
                .filter(v -> v.getDocument().getId().equals(documentId) && Boolean.TRUE.equals(v.getCurrentVersion()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Version courante introuvable"));
        DocumentAccessLog log = new DocumentAccessLog();
        log.setDocument(version.getDocument());
        log.setActionType("DOWNLOAD");
        accessLogRepository.save(log);
        return storageService.loadAsResource(version.getStoragePath());
    }
}
