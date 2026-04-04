package com.agora.assemblee.legislation.service;

import com.agora.assemblee.common.enums.DocumentClassificationLevel;
import com.agora.assemblee.common.enums.LegislativeTextStatus;
import com.agora.assemblee.common.exception.ResourceNotFoundException;
import com.agora.assemblee.documents.model.Document;
import com.agora.assemblee.documents.repository.DocumentRepository;
import com.agora.assemblee.institution.model.AssemblySession;
import com.agora.assemblee.institution.model.Committee;
import com.agora.assemblee.institution.model.Deputy;
import com.agora.assemblee.institution.repository.AssemblySessionRepository;
import com.agora.assemblee.institution.repository.CommitteeRepository;
import com.agora.assemblee.institution.repository.DeputyRepository;
import com.agora.assemblee.legislation.dto.*;
import com.agora.assemblee.legislation.model.*;
import com.agora.assemblee.legislation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LegislationService {

    private final LegislativeTextRepository textRepository;
    private final LegislativeTextVersionRepository versionRepository;
    private final AmendmentRepository amendmentRepository;
    private final CommissionReportRepository reportRepository;
    private final TextAssignmentRepository assignmentRepository;
    private final TextArticleRepository articleRepository;

    private final CommitteeRepository committeeRepository;
    private final DeputyRepository deputyRepository;
    private final AssemblySessionRepository assemblySessionRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public LegislativeTextResponse createText(LegislativeTextRequest request) {
        if (textRepository.existsByFilingNumber(request.filingNumber())) {
            throw new IllegalArgumentException("Un texte avec ce numéro de dépôt existe déjà");
        }

        LegislativeText text = new LegislativeText();
        text.setTitle(request.title());
        text.setFilingNumber(request.filingNumber());
        text.setOrigin(request.origin());
        text.setTextType(request.textType());
        text.setTheme(request.theme());
        text.setSummary(request.summary());
        text.setWorkflowStatus(LegislativeTextStatus.DRAFT);
        text.setConfidentialityLevel(
                request.confidentialityLevel() != null
                        ? request.confidentialityLevel()
                        : DocumentClassificationLevel.INTERNAL
        );

        if (request.assignedCommitteeId() != null) {
            Committee committee = committeeRepository.findById(request.assignedCommitteeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commission introuvable"));
            text.setAssignedCommittee(committee);
        }

        if (request.sponsoringDeputyId() != null) {
            Deputy deputy = deputyRepository.findById(request.sponsoringDeputyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Député introuvable"));
            text.setSponsoringDeputy(deputy);
        }

        return toTextResponse(textRepository.save(text));
    }

    @Transactional
    public LegislativeTextVersionResponse createVersion(VersionRequest request) {
        LegislativeText text = textRepository.findById(request.legislativeTextId())
                .orElseThrow(() -> new ResourceNotFoundException("Texte introuvable"));

        versionRepository.findByLegislativeTextIdAndCurrentVersionTrue(text.getId())
                .ifPresent(previous -> {
                    previous.setCurrentVersion(Boolean.FALSE);
                    versionRepository.save(previous);
                });

        LegislativeTextVersion version = new LegislativeTextVersion();
        version.setLegislativeText(text);
        version.setVersionNumber(request.versionNumber());
        version.setRichTextContent(request.richTextContent());
        version.setContentHash(request.contentHash());
        version.setCurrentVersion(Boolean.TRUE);
        version.setVersionLabel(request.versionLabel());
        version.setPublishable(Boolean.TRUE.equals(request.publishable()));

        LegislativeTextVersion saved = versionRepository.save(version);

        text.setCurrentVersion(saved);
        if (text.getWorkflowStatus() == LegislativeTextStatus.DRAFT) {
            text.setWorkflowStatus(LegislativeTextStatus.UNDER_REVIEW);
        }
        textRepository.save(text);

        return toVersionResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<LegislativeTextResponse> listTexts() {
        return textRepository.findAll()
                .stream()
                .map(this::toTextResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LegislativeTextResponse getText(Long id) {
        LegislativeText text = textRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Texte introuvable"));
        return toTextResponse(text);
    }

    @Transactional(readOnly = true)
    public List<LegislativeTextResponse> searchTexts(String keyword) {
        return textRepository
                .findByTitleContainingIgnoreCaseOrFilingNumberContainingIgnoreCaseOrderByCreatedAtDesc(keyword, keyword)
                .stream()
                .map(this::toTextResponse)
                .toList();
    }

    @Transactional
    public LegislativeTextResponse assignText(TextAssignmentRequest request) {
        LegislativeText text = textRepository.findById(request.legislativeTextId())
                .orElseThrow(() -> new ResourceNotFoundException("Texte introuvable"));

        Committee committee = committeeRepository.findById(request.committeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commission introuvable"));

        AssemblySession session = null;
        if (request.assemblySessionId() != null) {
            session = assemblySessionRepository.findById(request.assemblySessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session d'assemblée introuvable"));
        }

        TextAssignment assignment = new TextAssignment();
        assignment.setLegislativeText(text);
        assignment.setCommittee(committee);
        assignment.setAssemblySession(session);
        assignment.setAssignmentReason(request.assignmentReason());
        assignmentRepository.save(assignment);

        text.setAssignedCommittee(committee);
        text.setWorkflowStatus(LegislativeTextStatus.UNDER_REVIEW);
        textRepository.save(text);

        return toTextResponse(text);
    }

    @Transactional
    public AmendmentResponse createAmendment(AmendmentRequest request) {
        LegislativeText text = textRepository.findById(request.legislativeTextId())
                .orElseThrow(() -> new ResourceNotFoundException("Texte introuvable"));

        TextArticle article = null;
        if (request.targetArticleId() != null) {
            article = articleRepository.findById(request.targetArticleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Article introuvable"));
        }

        Deputy deputy = null;
        if (request.authorDeputyId() != null) {
            deputy = deputyRepository.findById(request.authorDeputyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Député auteur introuvable"));
        }

        Committee committee = null;
        if (request.committeeId() != null) {
            committee = committeeRepository.findById(request.committeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commission introuvable"));
        }

        Amendment amendment = new Amendment();
        amendment.setLegislativeText(text);
        amendment.setTargetArticle(article);
        amendment.setAuthorDeputy(deputy);
        amendment.setCommittee(committee);
        amendment.setAmendmentType(request.amendmentType());
        amendment.setJustification(request.justification());
        amendment.setProposedContent(request.proposedContent());

        return toAmendmentResponse(amendmentRepository.save(amendment));
    }

    @Transactional(readOnly = true)
    public List<AmendmentResponse> listAmendmentsByText(Long legislativeTextId) {
        return amendmentRepository.findByLegislativeTextIdOrderByCreatedAtDesc(legislativeTextId)
                .stream()
                .map(this::toAmendmentResponse)
                .toList();
    }

    @Transactional
    public CommissionReportResponse createCommissionReport(CommissionReportRequest request) {
        LegislativeText text = textRepository.findById(request.legislativeTextId())
                .orElseThrow(() -> new ResourceNotFoundException("Texte introuvable"));

        Committee committee = committeeRepository.findById(request.committeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commission introuvable"));

        Document document = null;
        if (request.mainDocumentId() != null) {
            document = documentRepository.findById(request.mainDocumentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Document principal introuvable"));
        }

        CommissionReport report = new CommissionReport();
        report.setLegislativeText(text);
        report.setCommittee(committee);
        report.setMainDocument(document);
        report.setRecommendationSummary(request.recommendationSummary());

        return toCommissionReportResponse(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public List<CommissionReportResponse> listReportsByText(Long legislativeTextId) {
        return reportRepository.findByLegislativeTextIdOrderByCreatedAtDesc(legislativeTextId)
                .stream()
                .map(this::toCommissionReportResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LegislativeTextVersionResponse> listVersionsByText(Long legislativeTextId) {
        return versionRepository.findByLegislativeTextIdOrderByVersionNumberDesc(legislativeTextId)
                .stream()
                .map(this::toVersionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LegislationDashboardResponse getDashboard() {
        return new LegislationDashboardResponse(
                textRepository.count(),
                textRepository.countByWorkflowStatus(LegislativeTextStatus.DRAFT),
                textRepository.countByWorkflowStatus(LegislativeTextStatus.UNDER_REVIEW),
                textRepository.countByWorkflowStatus(LegislativeTextStatus.ADOPTED),
                textRepository.countByWorkflowStatus(LegislativeTextStatus.REJECTED),
                textRepository.countByWorkflowStatus(LegislativeTextStatus.PUBLISHED),
                textRepository.countByWorkflowStatus(LegislativeTextStatus.ARCHIVED),
                assignmentRepository.count(),
                reportRepository.count(),
                amendmentRepository.count()
        );
    }

    @Transactional
    public LegislativeTextResponse updateWorkflowStatus(Long textId, LegislativeTextStatus status) {
        LegislativeText text = textRepository.findById(textId)
                .orElseThrow(() -> new ResourceNotFoundException("Texte introuvable"));

        text.setWorkflowStatus(status);
        return toTextResponse(textRepository.save(text));
    }

    private LegislativeTextResponse toTextResponse(LegislativeText text) {
        Integer currentVersionNumber = text.getCurrentVersion() != null
                ? text.getCurrentVersion().getVersionNumber()
                : null;

        Integer adoptedVersionNumber = text.getAdoptedVersion() != null
                ? text.getAdoptedVersion().getVersionNumber()
                : null;

        String sponsoringDeputyName = null;
        if (text.getSponsoringDeputy() != null) {
            String firstName = safe(text.getSponsoringDeputy().getFirstName());
            String lastName = safe(text.getSponsoringDeputy().getLastName());
            sponsoringDeputyName = (firstName + " " + lastName).trim();
        }

        return new LegislativeTextResponse(
                text.getId(),
                text.getTitle(),
                text.getFilingNumber(),
                text.getOrigin(),
                text.getTextType(),
                text.getTheme(),
                text.getSummary(),
                text.getWorkflowStatus(),
                text.getConfidentialityLevel(),
                text.getAssignedCommittee() != null ? text.getAssignedCommittee().getId() : null,
                text.getAssignedCommittee() != null ? text.getAssignedCommittee().getName() : null,
                text.getSponsoringDeputy() != null ? text.getSponsoringDeputy().getId() : null,
                sponsoringDeputyName,
                text.getCurrentVersion() != null ? text.getCurrentVersion().getId() : null,
                currentVersionNumber,
                text.getAdoptedVersion() != null ? text.getAdoptedVersion().getId() : null,
                adoptedVersionNumber,
                (int) versionRepository.countByLegislativeTextId(text.getId()),
                (int) articleRepository.countByTextVersionLegislativeTextId(text.getId()),
                (int) amendmentRepository.countByLegislativeTextId(text.getId()),
                (int) reportRepository.countByLegislativeTextId(text.getId()),
                (int) assignmentRepository.countByLegislativeTextId(text.getId()),
                text.getCreatedAt(),
                text.getUpdatedAt()
        );
    }

    private LegislativeTextVersionResponse toVersionResponse(LegislativeTextVersion version) {
        return new LegislativeTextVersionResponse(
                version.getId(),
                version.getLegislativeText() != null ? version.getLegislativeText().getId() : null,
                version.getVersionNumber(),
                version.getVersionLabel(),
                version.getContentHash(),
                version.getCurrentVersion(),
                version.getPublishable(),
                version.getCreatedAt(),
                version.getUpdatedAt()
        );
    }

    private AmendmentResponse toAmendmentResponse(Amendment amendment) {
        String authorName = null;
        if (amendment.getAuthorDeputy() != null) {
            authorName = (
                    safe(amendment.getAuthorDeputy().getFirstName()) + " " +
                            safe(amendment.getAuthorDeputy().getLastName())
            ).trim();
        }

        return new AmendmentResponse(
                amendment.getId(),
                amendment.getLegislativeText() != null ? amendment.getLegislativeText().getId() : null,
                amendment.getLegislativeText() != null ? amendment.getLegislativeText().getTitle() : null,
                amendment.getTargetArticle() != null ? amendment.getTargetArticle().getId() : null,
                amendment.getTargetArticle() != null ? amendment.getTargetArticle().getArticleNumber() : null,
                amendment.getAuthorDeputy() != null ? amendment.getAuthorDeputy().getId() : null,
                authorName,
                amendment.getCommittee() != null ? amendment.getCommittee().getId() : null,
                amendment.getCommittee() != null ? amendment.getCommittee().getName() : null,
                amendment.getAmendmentType(),
                amendment.getJustification(),
                amendment.getProposedContent(),
                amendment.getCreatedAt(),
                amendment.getUpdatedAt()
        );
    }

    private CommissionReportResponse toCommissionReportResponse(CommissionReport report) {
        return new CommissionReportResponse(
                report.getId(),
                report.getLegislativeText() != null ? report.getLegislativeText().getId() : null,
                report.getLegislativeText() != null ? report.getLegislativeText().getTitle() : null,
                report.getCommittee() != null ? report.getCommittee().getId() : null,
                report.getCommittee() != null ? report.getCommittee().getName() : null,
                report.getMainDocument() != null ? report.getMainDocument().getId() : null,
                report.getRecommendationSummary(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}