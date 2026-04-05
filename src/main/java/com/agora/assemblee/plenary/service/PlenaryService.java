package com.agora.assemblee.plenary.service;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.auth.repository.UserRepository;
import com.agora.assemblee.common.enums.AgendaItemStatus;
import com.agora.assemblee.common.enums.AttendanceStatus;
import com.agora.assemblee.common.enums.PlenarySessionStatus;
import com.agora.assemblee.common.enums.VoteChoice;
import com.agora.assemblee.common.enums.VoteMode;
import com.agora.assemblee.common.exception.ResourceNotFoundException;
import com.agora.assemblee.institution.model.AssemblySession;
import com.agora.assemblee.institution.model.Deputy;
import com.agora.assemblee.institution.repository.AssemblySessionRepository;
import com.agora.assemblee.institution.repository.DeputyRepository;
import com.agora.assemblee.legislation.model.CommissionReport;
import com.agora.assemblee.legislation.model.LegislativeText;
import com.agora.assemblee.legislation.model.LegislativeTextVersion;
import com.agora.assemblee.legislation.repository.CommissionReportRepository;
import com.agora.assemblee.legislation.repository.LegislativeTextRepository;
import com.agora.assemblee.legislation.repository.LegislativeTextVersionRepository;
import com.agora.assemblee.plenary.dto.AgendaItemRequest;
import com.agora.assemblee.plenary.dto.AgendaItemResponse;
import com.agora.assemblee.plenary.dto.CloseSessionRequest;
import com.agora.assemblee.plenary.dto.PlenarySessionDetailResponse;
import com.agora.assemblee.plenary.dto.PlenarySessionRequest;
import com.agora.assemblee.plenary.dto.PlenarySessionResponse;
import com.agora.assemblee.plenary.dto.QuorumResponse;
import com.agora.assemblee.plenary.dto.SessionAttendanceRequest;
import com.agora.assemblee.plenary.dto.SessionAttendanceResponse;
import com.agora.assemblee.plenary.dto.UpdateAgendaItemRequest;
import com.agora.assemblee.plenary.dto.UpdatePlenarySessionRequest;
import com.agora.assemblee.plenary.dto.VoteRecordRequest;
import com.agora.assemblee.plenary.dto.VoteRecordResponse;
import com.agora.assemblee.plenary.dto.VoteSummaryRequest;
import com.agora.assemblee.plenary.dto.VoteSummaryResponse;
import com.agora.assemblee.plenary.model.AgendaItem;
import com.agora.assemblee.plenary.model.PlenarySession;
import com.agora.assemblee.plenary.model.SessionAttendance;
import com.agora.assemblee.plenary.model.VoteRecord;
import com.agora.assemblee.plenary.model.VoteSummary;
import com.agora.assemblee.plenary.repository.AgendaItemRepository;
import com.agora.assemblee.plenary.repository.PlenarySessionRepository;
import com.agora.assemblee.plenary.repository.SessionAttendanceRepository;
import com.agora.assemblee.plenary.repository.VoteRecordRepository;
import com.agora.assemblee.plenary.repository.VoteSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlenaryService {

    private final PlenarySessionRepository plenarySessionRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final SessionAttendanceRepository sessionAttendanceRepository;
    private final VoteSummaryRepository voteSummaryRepository;
    private final VoteRecordRepository voteRecordRepository;

    private final AssemblySessionRepository assemblySessionRepository;
    private final UserRepository userRepository;
    private final DeputyRepository deputyRepository;
    private final LegislativeTextRepository legislativeTextRepository;
    private final CommissionReportRepository commissionReportRepository;
    private final LegislativeTextVersionRepository legislativeTextVersionRepository;

    @Transactional
    public PlenarySessionResponse createSession(PlenarySessionRequest request) {
        AssemblySession assemblySession = assemblySessionRepository.findById(request.assemblySessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session d’assemblée introuvable"));

        User presidingUser = null;
        if (request.presidingUserId() != null) {
            presidingUser = userRepository.findById(request.presidingUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Président de séance introuvable"));
        }

        PlenarySession session = new PlenarySession();
        session.setTitle(request.title().trim());
        session.setSessionType(request.sessionType().trim());
        session.setAssemblySession(assemblySession);
        session.setPresidingUser(presidingUser);
        session.setLocation(safeTrim(request.location()));
        session.setStartDateTime(request.startDateTime());
        session.setEndDateTime(request.endDateTime());
        session.setExpectedMembersCount(resolveExpectedMembersCount(request.expectedMembersCount()));
        session.setQuorumThresholdPercent(resolveQuorumThresholdPercent(request.quorumThresholdPercent()));
        session.setPlenaryStatus(resolveInitialStatus(request.startDateTime()));
        session.setPresentMembersCount(0);
        session.setQuorumReached(Boolean.FALSE);

        validateSessionDates(session.getStartDateTime(), session.getEndDateTime());

        PlenarySession saved = plenarySessionRepository.save(session);
        return toSessionResponse(saved);
    }

    @Transactional
    public PlenarySessionResponse updateSession(Long sessionId, UpdatePlenarySessionRequest request) {
        PlenarySession session = getSessionOrThrow(sessionId);

        if (request.title() != null && !request.title().isBlank()) {
            session.setTitle(request.title().trim());
        }

        if (request.sessionType() != null && !request.sessionType().isBlank()) {
            session.setSessionType(request.sessionType().trim());
        }

        if (request.assemblySessionId() != null) {
            AssemblySession assemblySession = assemblySessionRepository.findById(request.assemblySessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session d’assemblée introuvable"));
            session.setAssemblySession(assemblySession);
        }

        if (request.presidingUserId() != null) {
            User presidingUser = userRepository.findById(request.presidingUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Président de séance introuvable"));
            session.setPresidingUser(presidingUser);
        }

        if (request.location() != null) {
            session.setLocation(safeTrim(request.location()));
        }

        if (request.startDateTime() != null) {
            session.setStartDateTime(request.startDateTime());
        }

        if (request.endDateTime() != null) {
            session.setEndDateTime(request.endDateTime());
        }

        if (request.expectedMembersCount() != null) {
            session.setExpectedMembersCount(resolveExpectedMembersCount(request.expectedMembersCount()));
        }

        if (request.quorumThresholdPercent() != null) {
            session.setQuorumThresholdPercent(resolveQuorumThresholdPercent(request.quorumThresholdPercent()));
        }

        validateSessionDates(session.getStartDateTime(), session.getEndDateTime());
        refreshQuorum(session);

        return toSessionResponse(plenarySessionRepository.save(session));
    }

    @Transactional
    public PlenarySessionResponse openSession(Long sessionId) {
        PlenarySession session = getSessionOrThrow(sessionId);

        if (session.getPlenaryStatus() == PlenarySessionStatus.CLOSED
                || session.getPlenaryStatus() == PlenarySessionStatus.ARCHIVED) {
            throw new IllegalStateException("Impossible d’ouvrir une séance clôturée ou archivée");
        }

        refreshQuorum(session);
        session.setPlenaryStatus(PlenarySessionStatus.OPEN);

        if (session.getStartDateTime() == null) {
            session.setStartDateTime(LocalDateTime.now());
        }

        return toSessionResponse(plenarySessionRepository.save(session));
    }

    @Transactional
    public PlenarySessionResponse closeSession(Long sessionId, CloseSessionRequest request) {
        PlenarySession session = getSessionOrThrow(sessionId);

        if (session.getPlenaryStatus() != PlenarySessionStatus.OPEN
                && session.getPlenaryStatus() != PlenarySessionStatus.SUSPENDED) {
            throw new IllegalStateException("Seule une séance ouverte ou suspendue peut être clôturée");
        }

        session.setPlenaryStatus(PlenarySessionStatus.CLOSED);
        session.setClosingSummary(request.closingSummary().trim());

        if (session.getEndDateTime() == null) {
            session.setEndDateTime(LocalDateTime.now());
        }

        refreshQuorum(session);
        return toSessionResponse(plenarySessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public PlenarySessionDetailResponse getSessionDetail(Long sessionId) {
        PlenarySession session = getSessionOrThrow(sessionId);

        return new PlenarySessionDetailResponse(
                toSessionResponse(session),
                getQuorum(sessionId),
                listAgendaItems(sessionId),
                listAttendances(sessionId),
                listVoteSummariesBySession(sessionId),
                session.getClosingSummary()
        );
    }

    @Transactional(readOnly = true)
    public Page<PlenarySessionResponse> listSessions(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasStatus = status != null && !status.isBlank();

        Page<PlenarySession> result;

        if (hasKeyword && hasStatus) {
            result = plenarySessionRepository.findByTitleContainingIgnoreCaseAndPlenaryStatus(
                    keyword.trim(),
                    PlenarySessionStatus.valueOf(status.trim().toUpperCase(Locale.ROOT)),
                    pageable
            );
        } else if (hasKeyword) {
            result = plenarySessionRepository.findByTitleContainingIgnoreCase(keyword.trim(), pageable);
        } else if (hasStatus) {
            result = plenarySessionRepository.findByPlenaryStatus(
                    PlenarySessionStatus.valueOf(status.trim().toUpperCase(Locale.ROOT)),
                    pageable
            );
        } else {
            result = plenarySessionRepository.findAll(pageable);
        }

        return result.map(this::toSessionResponse);
    }

    @Transactional
    public AgendaItemResponse addAgendaItem(Long sessionId, AgendaItemRequest request) {
        PlenarySession session = getSessionOrThrow(sessionId);

        if (agendaItemRepository.existsByPlenarySessionIdAndSortOrder(sessionId, request.sortOrder())) {
            throw new IllegalStateException("Un point de l’ordre du jour existe déjà avec cet ordre");
        }

        LegislativeText legislativeText = null;
        if (request.legislativeTextId() != null) {
            legislativeText = legislativeTextRepository.findById(request.legislativeTextId())
                    .orElseThrow(() -> new ResourceNotFoundException("Texte législatif introuvable"));
        }

        CommissionReport commissionReport = null;
        if (request.commissionReportId() != null) {
            commissionReport = commissionReportRepository.findById(request.commissionReportId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rapport de commission introuvable"));
        }

        AgendaItem item = new AgendaItem();
        item.setPlenarySession(session);
        item.setLegislativeText(legislativeText);
        item.setCommissionReport(commissionReport);
        item.setLabel(request.label().trim());
        item.setSortOrder(request.sortOrder());
        item.setAgendaItemStatus(AgendaItemStatus.PLANNED);

        return toAgendaItemResponse(agendaItemRepository.save(item));
    }

    @Transactional
    public AgendaItemResponse updateAgendaItem(Long agendaItemId, UpdateAgendaItemRequest request) {
        AgendaItem item = agendaItemRepository.findById(agendaItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Point de l’ordre du jour introuvable"));

        if (request.legislativeTextId() != null) {
            LegislativeText legislativeText = legislativeTextRepository.findById(request.legislativeTextId())
                    .orElseThrow(() -> new ResourceNotFoundException("Texte législatif introuvable"));
            item.setLegislativeText(legislativeText);
        }

        if (request.commissionReportId() != null) {
            CommissionReport commissionReport = commissionReportRepository.findById(request.commissionReportId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rapport de commission introuvable"));
            item.setCommissionReport(commissionReport);
        }

        if (request.label() != null && !request.label().isBlank()) {
            item.setLabel(request.label().trim());
        }

        if (request.sortOrder() != null) {
            boolean duplicateSortOrder = agendaItemRepository.existsByPlenarySessionIdAndSortOrder(
                    item.getPlenarySession().getId(),
                    request.sortOrder()
            );
            if (duplicateSortOrder && !Objects.equals(item.getSortOrder(), request.sortOrder())) {
                throw new IllegalStateException("Un autre point de l’ordre du jour utilise déjà cet ordre");
            }
            item.setSortOrder(request.sortOrder());
        }

        if (request.decisionSummary() != null) {
            item.setDecisionSummary(safeTrim(request.decisionSummary()));
        }

        if (request.status() != null && !request.status().isBlank()) {
            item.setAgendaItemStatus(
                    AgendaItemStatus.valueOf(request.status().trim().toUpperCase(Locale.ROOT))
            );
        }

        return toAgendaItemResponse(agendaItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<AgendaItemResponse> listAgendaItems(Long sessionId) {
        getSessionOrThrow(sessionId);
        return agendaItemRepository.findByPlenarySessionIdOrderBySortOrderAsc(sessionId)
                .stream()
                .map(this::toAgendaItemResponse)
                .toList();
    }

    @Transactional
    public SessionAttendanceResponse recordAttendance(Long sessionId, SessionAttendanceRequest request) {
        PlenarySession session = getSessionOrThrow(sessionId);

        Deputy deputy = deputyRepository.findById(request.deputyId())
                .orElseThrow(() -> new ResourceNotFoundException("Député introuvable"));

        User recordedBy = null;
        if (request.recordedByUserId() != null) {
            recordedBy = userRepository.findById(request.recordedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur enregistreur introuvable"));
        }

        AttendanceStatus attendanceStatus = AttendanceStatus.valueOf(
                request.attendanceStatus().trim().toUpperCase(Locale.ROOT)
        );

        SessionAttendance attendance = sessionAttendanceRepository
                .findByPlenarySessionIdAndDeputyId(sessionId, request.deputyId())
                .orElseGet(SessionAttendance::new);

        attendance.setPlenarySession(session);
        attendance.setDeputy(deputy);
        attendance.setAttendanceStatus(attendanceStatus);
        attendance.setPresent(isPresent(attendanceStatus));
        attendance.setAbsenceJustification(safeTrim(request.absenceJustification()));
        attendance.setRecordedBy(recordedBy);

        SessionAttendance saved = sessionAttendanceRepository.save(attendance);
        refreshQuorum(session);

        return toAttendanceResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SessionAttendanceResponse> listAttendances(Long sessionId) {
        getSessionOrThrow(sessionId);
        return sessionAttendanceRepository.findByPlenarySessionIdOrderByDeputyIdAsc(sessionId)
                .stream()
                .map(this::toAttendanceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuorumResponse getQuorum(Long sessionId) {
        PlenarySession session = getSessionOrThrow(sessionId);

        int expected = resolveExpectedMembersCount(session.getExpectedMembersCount());
        int present = Math.toIntExact(
                sessionAttendanceRepository.countByPlenarySessionIdAndAttendanceStatusIn(
                        sessionId,
                        List.of(AttendanceStatus.PRESENT, AttendanceStatus.LATE)
                )
        );
        int thresholdPercent = resolveQuorumThresholdPercent(session.getQuorumThresholdPercent());
        int required = calculateRequiredQuorum(expected, thresholdPercent);
        boolean reached = present >= required;

        return new QuorumResponse(
                sessionId,
                expected,
                present,
                thresholdPercent,
                required,
                reached
        );
    }

    @Transactional
    public VoteSummaryResponse createVoteSummary(Long agendaItemId, VoteSummaryRequest request) {
        AgendaItem agendaItem = agendaItemRepository.findById(agendaItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Point de l’ordre du jour introuvable"));

        voteSummaryRepository.findByAgendaItemId(agendaItemId).ifPresent(existing -> {
            throw new IllegalStateException("Un résumé de vote existe déjà pour ce point");
        });

        VoteSummary summary = new VoteSummary();
        summary.setAgendaItem(agendaItem);
        summary.setVoteMode(VoteMode.valueOf(request.voteMode().trim().toUpperCase(Locale.ROOT)));
        summary.setDecisionLabel(safeTrim(request.decisionLabel()));

        if (request.adoptedTextVersionId() != null) {
            LegislativeTextVersion version = legislativeTextVersionRepository.findById(request.adoptedTextVersionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Version du texte adoptée introuvable"));
            summary.setAdoptedTextVersion(version);
        }

        VoteSummary saved = voteSummaryRepository.save(summary);
        return toVoteSummaryResponse(saved);
    }

    @Transactional
    public VoteRecordResponse recordVote(Long voteSummaryId, VoteRecordRequest request) {
        VoteSummary summary = voteSummaryRepository.findById(voteSummaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Résumé de vote introuvable"));

        Deputy deputy = deputyRepository.findById(request.deputyId())
                .orElseThrow(() -> new ResourceNotFoundException("Député introuvable"));

        VoteChoice choice = VoteChoice.valueOf(request.choice().trim().toUpperCase(Locale.ROOT));

        VoteRecord voteRecord = voteRecordRepository.findByVoteSummaryIdAndDeputyId(voteSummaryId, request.deputyId())
                .orElseGet(VoteRecord::new);

        voteRecord.setVoteSummary(summary);
        voteRecord.setDeputy(deputy);
        voteRecord.setVoteChoice(choice);

        VoteRecord saved = voteRecordRepository.save(voteRecord);

        recomputeVoteSummary(summary);

        return toVoteRecordResponse(saved);
    }

    @Transactional(readOnly = true)
    public VoteSummaryResponse getVoteSummary(Long voteSummaryId) {
        VoteSummary summary = voteSummaryRepository.findById(voteSummaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Résumé de vote introuvable"));
        return toVoteSummaryResponse(summary);
    }

    @Transactional(readOnly = true)
    public List<VoteSummaryResponse> listVoteSummariesBySession(Long sessionId) {
        getSessionOrThrow(sessionId);
        return voteSummaryRepository.findByAgendaItemPlenarySessionId(sessionId)
                .stream()
                .map(this::toVoteSummaryResponse)
                .toList();
    }

    private void recomputeVoteSummary(VoteSummary summary) {
        List<VoteRecord> votes = voteRecordRepository.findByVoteSummaryId(summary.getId());

        int votesFor = 0;
        int votesAgainst = 0;
        int abstentions = 0;
        int absents = 0;

        for (VoteRecord vote : votes) {
            switch (vote.getVoteChoice()) {
                case FOR -> votesFor++;
                case AGAINST -> votesAgainst++;
                case ABSTAIN -> abstentions++;
                case ABSENT -> absents++;
            }
        }

        summary.setVotesForCount(votesFor);
        summary.setVotesAgainstCount(votesAgainst);
        summary.setAbstentionCount(abstentions);
        summary.setAbsentCount(absents);
        summary.setAdopted(votesFor > votesAgainst);

        voteSummaryRepository.save(summary);
    }

    private void refreshQuorum(PlenarySession session) {
        int expected = resolveExpectedMembersCount(session.getExpectedMembersCount());
        int present = Math.toIntExact(
                sessionAttendanceRepository.countByPlenarySessionIdAndAttendanceStatusIn(
                        session.getId(),
                        List.of(AttendanceStatus.PRESENT, AttendanceStatus.LATE)
                )
        );
        int thresholdPercent = resolveQuorumThresholdPercent(session.getQuorumThresholdPercent());

        session.setExpectedMembersCount(expected);
        session.setPresentMembersCount(present);
        session.setQuorumReached(present >= calculateRequiredQuorum(expected, thresholdPercent));
    }

    private int calculateRequiredQuorum(int expectedMembersCount, int quorumThresholdPercent) {
        if (expectedMembersCount <= 0) {
            return 0;
        }
        return (int) Math.ceil(expectedMembersCount * (quorumThresholdPercent / 100.0));
    }

    private boolean isPresent(AttendanceStatus status) {
        return status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE;
    }

    private int resolveExpectedMembersCount(Integer value) {
        if (value != null && value > 0) {
            return value;
        }
        long deputyCount = deputyRepository.count();
        return (int) Math.max(deputyCount, 0);
    }

    private int resolveQuorumThresholdPercent(Integer value) {
        if (value == null || value <= 0) {
            return 50;
        }
        if (value > 100) {
            throw new IllegalArgumentException("Le pourcentage du quorum ne peut pas dépasser 100");
        }
        return value;
    }

    private void validateSessionDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }

    private PlenarySessionStatus resolveInitialStatus(LocalDateTime startDateTime) {
        return startDateTime == null ? PlenarySessionStatus.DRAFT : PlenarySessionStatus.PLANNED;
    }

    private PlenarySession getSessionOrThrow(Long sessionId) {
        return plenarySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Séance plénière introuvable"));
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private PlenarySessionResponse toSessionResponse(PlenarySession session) {
        long agendaCount = agendaItemRepository.countByPlenarySessionId(session.getId());

        return new PlenarySessionResponse(
                session.getId(),
                session.getTitle(),
                session.getSessionType(),
                session.getAssemblySession() != null ? session.getAssemblySession().getId() : null,
                resolveAssemblySessionLabel(session.getAssemblySession()),
                session.getPresidingUser() != null ? session.getPresidingUser().getId() : null,
                resolveUserDisplayName(session.getPresidingUser()),
                session.getLocation(),
                session.getPlenaryStatus() != null ? session.getPlenaryStatus().name() : null,
                session.getStartDateTime(),
                session.getEndDateTime(),
                session.getExpectedMembersCount(),
                session.getPresentMembersCount(),
                session.getQuorumThresholdPercent(),
                session.getQuorumReached(),
                Math.toIntExact(agendaCount)
        );
    }

    private AgendaItemResponse toAgendaItemResponse(AgendaItem item) {
        return new AgendaItemResponse(
                item.getId(),
                item.getPlenarySession() != null ? item.getPlenarySession().getId() : null,
                item.getLegislativeText() != null ? item.getLegislativeText().getId() : null,
                resolveLegislativeTextTitle(item.getLegislativeText()),
                item.getCommissionReport() != null ? item.getCommissionReport().getId() : null,
                resolveCommissionReportTitle(item.getCommissionReport()),
                item.getSortOrder(),
                item.getLabel(),
                item.getAgendaItemStatus() != null ? item.getAgendaItemStatus().name() : null,
                item.getDecisionSummary()
        );
    }

    private SessionAttendanceResponse toAttendanceResponse(SessionAttendance attendance) {
        return new SessionAttendanceResponse(
                attendance.getId(),
                attendance.getPlenarySession() != null ? attendance.getPlenarySession().getId() : null,
                attendance.getDeputy() != null ? attendance.getDeputy().getId() : null,
                resolveDeputyDisplayName(attendance.getDeputy()),
                attendance.getAttendanceStatus() != null ? attendance.getAttendanceStatus().name() : null,
                attendance.getPresent(),
                attendance.getAbsenceJustification(),
                attendance.getRecordedBy() != null ? attendance.getRecordedBy().getId() : null,
                resolveUserDisplayName(attendance.getRecordedBy())
        );
    }

    private VoteSummaryResponse toVoteSummaryResponse(VoteSummary summary) {
        int totalVotes = n(summary.getVotesForCount())
                + n(summary.getVotesAgainstCount())
                + n(summary.getAbstentionCount())
                + n(summary.getAbsentCount());

        return new VoteSummaryResponse(
                summary.getId(),
                summary.getAgendaItem() != null ? summary.getAgendaItem().getId() : null,
                summary.getAgendaItem() != null ? summary.getAgendaItem().getLabel() : null,
                summary.getVoteMode() != null ? summary.getVoteMode().name() : null,
                n(summary.getVotesForCount()),
                n(summary.getVotesAgainstCount()),
                n(summary.getAbstentionCount()),
                n(summary.getAbsentCount()),
                totalVotes,
                summary.getAdopted(),
                summary.getDecisionLabel(),
                summary.getAdoptedTextVersion() != null ? summary.getAdoptedTextVersion().getId() : null
        );
    }

    private VoteRecordResponse toVoteRecordResponse(VoteRecord voteRecord) {
        return new VoteRecordResponse(
                voteRecord.getId(),
                voteRecord.getVoteSummary() != null ? voteRecord.getVoteSummary().getId() : null,
                voteRecord.getDeputy() != null ? voteRecord.getDeputy().getId() : null,
                resolveDeputyDisplayName(voteRecord.getDeputy()),
                voteRecord.getVoteChoice() != null ? voteRecord.getVoteChoice().name() : null
        );
    }

    private String resolveUserDisplayName(User user) {
        if (user == null) {
            return null;
        }

        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName();
        }

        if (user.getLoginAlias() != null && !user.getLoginAlias().isBlank()) {
            return user.getLoginAlias();
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail();
        }

        return "Utilisateur #" + user.getId();
    }

    private String resolveDeputyDisplayName(Deputy deputy) {
        if (deputy == null) {
            return null;
        }

        try {
            String firstName = (String) deputy.getClass().getMethod("getFirstName").invoke(deputy);
            String lastName = (String) deputy.getClass().getMethod("getLastName").invoke(deputy);
            String fullName = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
            if (!fullName.isBlank()) {
                return fullName;
            }
        } catch (Exception ignored) {
        }

        try {
            String fullName = (String) deputy.getClass().getMethod("getFullName").invoke(deputy);
            if (fullName != null && !fullName.isBlank()) {
                return fullName;
            }
        } catch (Exception ignored) {
        }

        return "Député #" + deputy.getId();
    }

    private String resolveAssemblySessionLabel(AssemblySession assemblySession) {
        if (assemblySession == null) {
            return null;
        }

        try {
            String name = (String) assemblySession.getClass().getMethod("getName").invoke(assemblySession);
            if (name != null && !name.isBlank()) {
                return name;
            }
        } catch (Exception ignored) {
        }

        try {
            String label = (String) assemblySession.getClass().getMethod("getLabel").invoke(assemblySession);
            if (label != null && !label.isBlank()) {
                return label;
            }
        } catch (Exception ignored) {
        }

        return "Session #" + assemblySession.getId();
    }

    private String resolveLegislativeTextTitle(LegislativeText legislativeText) {
        if (legislativeText == null) {
            return null;
        }

        if (legislativeText.getTitle() != null && !legislativeText.getTitle().isBlank()) {
            return legislativeText.getTitle();
        }

        if (legislativeText.getFilingNumber() != null && !legislativeText.getFilingNumber().isBlank()) {
            return legislativeText.getFilingNumber();
        }

        return "Texte #" + legislativeText.getId();
    }

    private String resolveCommissionReportTitle(CommissionReport report) {
        if (report == null) {
            return null;
        }

        try {
            String title = (String) report.getClass().getMethod("getTitle").invoke(report);
            if (title != null && !title.isBlank()) {
                return title;
            }
        } catch (Exception ignored) {
        }

        return "Rapport #" + report.getId();
    }

    private int n(Integer value) {
        return value == null ? 0 : value;
    }
}