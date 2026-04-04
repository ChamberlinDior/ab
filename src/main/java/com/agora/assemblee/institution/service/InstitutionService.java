package com.agora.assemblee.institution.service;

import com.agora.assemblee.common.exception.ResourceNotFoundException;
import com.agora.assemblee.institution.dto.AssemblySessionMiniResponse;
import com.agora.assemblee.institution.dto.AssemblySessionRequest;
import com.agora.assemblee.institution.dto.AssemblySessionResponse;
import com.agora.assemblee.institution.dto.CommitteeMembershipRequest;
import com.agora.assemblee.institution.dto.CommitteeMembershipResponse;
import com.agora.assemblee.institution.dto.CommitteeMiniResponse;
import com.agora.assemblee.institution.dto.CommitteeRequest;
import com.agora.assemblee.institution.dto.CommitteeResponse;
import com.agora.assemblee.institution.dto.DeputyMiniResponse;
import com.agora.assemblee.institution.dto.DeputyRequest;
import com.agora.assemblee.institution.dto.DeputyResponse;
import com.agora.assemblee.institution.dto.InstitutionSummaryResponse;
import com.agora.assemblee.institution.dto.ParliamentaryGroupMiniResponse;
import com.agora.assemblee.institution.dto.ParliamentaryGroupRequest;
import com.agora.assemblee.institution.dto.ParliamentaryGroupResponse;
import com.agora.assemblee.institution.model.AssemblySession;
import com.agora.assemblee.institution.model.Committee;
import com.agora.assemblee.institution.model.CommitteeMembership;
import com.agora.assemblee.institution.model.Deputy;
import com.agora.assemblee.institution.model.ParliamentaryGroup;
import com.agora.assemblee.institution.repository.AssemblySessionRepository;
import com.agora.assemblee.institution.repository.CommitteeMembershipRepository;
import com.agora.assemblee.institution.repository.CommitteeRepository;
import com.agora.assemblee.institution.repository.DeputyRepository;
import com.agora.assemblee.institution.repository.ParliamentaryGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final DeputyRepository deputyRepository;
    private final ParliamentaryGroupRepository groupRepository;
    private final AssemblySessionRepository sessionRepository;
    private final CommitteeRepository committeeRepository;
    private final CommitteeMembershipRepository membershipRepository;

    @Transactional(readOnly = true)
    public InstitutionSummaryResponse getSummary() {
        AssemblySession activeSession = sessionRepository
                .findFirstByStatusIgnoreCaseOrderByStartDateDesc("ACTIVE")
                .orElse(null);

        return new InstitutionSummaryResponse(
                deputyRepository.count(),
                deputyRepository.countByActiveTrue(),
                committeeRepository.count(),
                committeeRepository.countByActiveTrue(),
                membershipRepository.count(),
                groupRepository.count(),
                toAssemblySessionMini(activeSession)
        );
    }

    @Transactional
    public AssemblySessionResponse createAssemblySession(AssemblySessionRequest request) {
        String normalizedStatus = normalizeUpper(request.status(), "ACTIVE");

        if ("ACTIVE".equals(normalizedStatus)) {
            List<AssemblySession> allSessions = sessionRepository.findAll();
            for (AssemblySession existing : allSessions) {
                if ("ACTIVE".equalsIgnoreCase(existing.getStatus())) {
                    existing.setStatus("CLOSED");
                }
            }
            sessionRepository.saveAll(allSessions);
        }

        AssemblySession session = new AssemblySession();
        session.setTitle(trimToNull(request.title()));
        session.setSessionType(normalizeUpper(request.sessionType(), "ORDINARY"));
        session.setLegislatureLabel(trimToNull(request.legislatureLabel()));
        session.setYearLabel(request.yearLabel());
        session.setStartDate(request.startDate());
        session.setEndDate(request.endDate());
        session.setStatus(normalizedStatus);
        session.setOpeningDecreeReference(trimToNull(request.openingDecreeReference()));
        session.setNotes(trimToNull(request.notes()));

        return toAssemblySessionResponse(sessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public List<AssemblySessionResponse> listAssemblySessions() {
        return sessionRepository.findAllByOrderByStartDateDescIdDesc()
                .stream()
                .map(this::toAssemblySessionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssemblySessionResponse getActiveAssemblySession() {
        AssemblySession session = sessionRepository
                .findFirstByStatusIgnoreCaseOrderByStartDateDesc("ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("Aucune session active"));
        return toAssemblySessionResponse(session);
    }

    @Transactional
    public ParliamentaryGroupResponse createParliamentaryGroup(ParliamentaryGroupRequest request) {
        if (groupRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Un groupe portant ce nom existe déjà");
        }

        ParliamentaryGroup group = new ParliamentaryGroup();
        group.setName(trimToNull(request.name()));
        group.setAcronym(trimToNull(request.acronym()));
        group.setDescription(trimToNull(request.description()));
        group.setLeaderName(trimToNull(request.leaderName()));
        group.setContactEmail(trimToNull(request.contactEmail()));
        group.setContactPhone(trimToNull(request.contactPhone()));
        group.setOfficial(request.official() == null ? Boolean.TRUE : request.official());

        return toParliamentaryGroupResponse(groupRepository.save(group));
    }

    @Transactional(readOnly = true)
    public List<ParliamentaryGroupResponse> listParliamentaryGroups() {
        return groupRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toParliamentaryGroupResponse)
                .toList();
    }

    @Transactional
    public DeputyResponse createDeputy(DeputyRequest request) {
        if (deputyRepository.existsByDeputyNumberIgnoreCase(request.deputyNumber())) {
            throw new IllegalArgumentException("Le numéro du député existe déjà");
        }

        AssemblySession currentSession = sessionRepository.findById(request.currentSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable"));

        ParliamentaryGroup group = null;
        if (request.parliamentaryGroupId() != null) {
            group = groupRepository.findById(request.parliamentaryGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Groupe introuvable"));
        }

        Deputy deputy = new Deputy();
        deputy.setFirstName(trimToNull(request.firstName()));
        deputy.setLastName(trimToNull(request.lastName()));
        deputy.setDeputyNumber(trimToNull(request.deputyNumber()));
        deputy.setGender(normalizeUpperNullable(request.gender()));
        deputy.setConstituency(trimToNull(request.constituency()));
        deputy.setProvince(trimToNull(request.province()));
        deputy.setDistrict(trimToNull(request.district()));
        deputy.setPoliticalParty(trimToNull(request.politicalParty()));
        deputy.setMandateStatus(normalizeUpper(request.mandateStatus(), "EN_FONCTION"));
        deputy.setEmail(trimToNull(request.email()));
        deputy.setPhoneNumber(trimToNull(request.phoneNumber()));
        deputy.setWhatsappNumber(trimToNull(request.whatsappNumber()));
        deputy.setPhotoUrl(trimToNull(request.photoUrl()));
        deputy.setSeatNumber(trimToNull(request.seatNumber()));
        deputy.setOfficialAddress(trimToNull(request.officialAddress()));
        deputy.setActive(request.active() == null ? Boolean.TRUE : request.active());
        deputy.setCurrentSession(currentSession);
        deputy.setParliamentaryGroup(group);

        return toDeputyResponse(deputyRepository.save(deputy));
    }

    @Transactional(readOnly = true)
    public List<DeputyResponse> listDeputies(Boolean activeOnly, Long sessionId) {
        List<Deputy> deputies;

        if (sessionId != null && Boolean.TRUE.equals(activeOnly)) {
            deputies = deputyRepository.findByCurrentSessionIdAndActiveTrueOrderByLastNameAscFirstNameAsc(sessionId);
        } else if (sessionId != null) {
            deputies = deputyRepository.findByCurrentSessionIdOrderByLastNameAscFirstNameAsc(sessionId);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            deputies = deputyRepository.findByActiveTrueOrderByLastNameAscFirstNameAsc();
        } else {
            deputies = deputyRepository.findAllByOrderByLastNameAscFirstNameAsc();
        }

        return deputies.stream().map(this::toDeputyResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DeputyResponse> searchDeputies(String keyword, Boolean activeOnly, Long sessionId) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        if (safeKeyword.isBlank()) {
            return listDeputies(activeOnly, sessionId);
        }

        List<Deputy> deputies = sessionId != null
                ? deputyRepository.searchBySession(safeKeyword, sessionId)
                : deputyRepository.search(safeKeyword);

        if (Boolean.TRUE.equals(activeOnly)) {
            deputies = deputies.stream()
                    .filter(d -> Boolean.TRUE.equals(d.getActive()))
                    .toList();
        }

        return deputies.stream().map(this::toDeputyResponse).toList();
    }

    @Transactional(readOnly = true)
    public DeputyResponse getDeputy(Long deputyId) {
        Deputy deputy = deputyRepository.findById(deputyId)
                .orElseThrow(() -> new ResourceNotFoundException("Député introuvable"));
        return toDeputyResponse(deputy);
    }

    @Transactional(readOnly = true)
    public List<CommitteeMembershipResponse> listDeputyMemberships(Long deputyId) {
        deputyRepository.findById(deputyId)
                .orElseThrow(() -> new ResourceNotFoundException("Député introuvable"));

        return membershipRepository.findByDeputyIdOrderByStartDateDescIdDesc(deputyId)
                .stream()
                .map(this::toCommitteeMembershipResponse)
                .toList();
    }

    @Transactional
    public CommitteeResponse createCommittee(CommitteeRequest request) {
        if (committeeRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Une commission portant ce nom existe déjà");
        }

        AssemblySession activeSession = sessionRepository.findById(request.activeSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable"));

        Committee committee = new Committee();
        committee.setName(trimToNull(request.name()));
        committee.setCode(trimToNull(request.code()));
        committee.setCommitteeType(normalizeUpperNullable(request.committeeType()));
        committee.setThematicScope(trimToNull(request.thematicScope()));
        committee.setDescription(trimToNull(request.description()));
        committee.setRoom(trimToNull(request.room()));
        committee.setContactEmail(trimToNull(request.contactEmail()));
        committee.setContactPhone(trimToNull(request.contactPhone()));
        committee.setActive(request.active() == null ? Boolean.TRUE : request.active());
        committee.setActiveSession(activeSession);

        return toCommitteeResponse(committeeRepository.save(committee));
    }

    @Transactional(readOnly = true)
    public List<CommitteeResponse> listCommittees(Long sessionId, Boolean activeOnly) {
        List<Committee> committees;

        if (sessionId != null) {
            committees = committeeRepository.findByActiveSessionIdOrderByNameAsc(sessionId);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            committees = committeeRepository.findByActiveTrueOrderByNameAsc();
        } else {
            committees = committeeRepository.findAllByOrderByNameAsc();
        }

        if (Boolean.TRUE.equals(activeOnly) && sessionId != null) {
            committees = committees.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getActive()))
                    .toList();
        }

        return committees.stream().map(this::toCommitteeResponse).toList();
    }

    @Transactional(readOnly = true)
    public CommitteeResponse getCommittee(Long committeeId) {
        Committee committee = committeeRepository.findById(committeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission introuvable"));
        return toCommitteeResponse(committee);
    }

    @Transactional
    public CommitteeMembershipResponse assignDeputyToCommittee(CommitteeMembershipRequest request) {
        if (membershipRepository.existsByCommitteeIdAndDeputyIdAndAssemblySessionId(
                request.committeeId(),
                request.deputyId(),
                request.assemblySessionId()
        )) {
            throw new IllegalArgumentException("Cette affectation existe déjà pour la session sélectionnée");
        }

        Committee committee = committeeRepository.findById(request.committeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commission introuvable"));

        Deputy deputy = deputyRepository.findById(request.deputyId())
                .orElseThrow(() -> new ResourceNotFoundException("Député introuvable"));

        AssemblySession session = sessionRepository.findById(request.assemblySessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session introuvable"));

        CommitteeMembership membership = new CommitteeMembership();
        membership.setCommittee(committee);
        membership.setDeputy(deputy);
        membership.setAssemblySession(session);
        membership.setOfficeRole(normalizeUpper(request.officeRole(), "MEMBRE"));
        membership.setStartDate(request.startDate());
        membership.setEndDate(request.endDate());
        membership.setPrimaryMembership(Boolean.TRUE.equals(request.primaryMembership()));
        membership.setBureauMember(Boolean.TRUE.equals(request.bureauMember()));
        membership.setNotes(trimToNull(request.notes()));

        return toCommitteeMembershipResponse(membershipRepository.save(membership));
    }

    @Transactional(readOnly = true)
    public List<CommitteeMembershipResponse> listCommitteeMemberships(Long committeeId) {
        committeeRepository.findById(committeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission introuvable"));

        return membershipRepository
                .findByCommitteeIdOrderByPrimaryMembershipDescBureauMemberDescOfficeRoleAscDeputyLastNameAscDeputyFirstNameAsc(committeeId)
                .stream()
                .map(this::toCommitteeMembershipResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommitteeMembershipResponse> listCommitteeBureau(Long committeeId) {
        committeeRepository.findById(committeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission introuvable"));

        return membershipRepository
                .findByCommitteeIdAndBureauMemberTrueOrderByOfficeRoleAscDeputyLastNameAscDeputyFirstNameAsc(committeeId)
                .stream()
                .map(this::toCommitteeMembershipResponse)
                .toList();
    }

    private DeputyResponse toDeputyResponse(Deputy deputy) {
        long membershipsCount = deputy.getId() == null ? 0 : membershipRepository.countByDeputyId(deputy.getId());
        long bureauMembershipsCount = deputy.getId() == null ? 0 : membershipRepository.countByDeputyIdAndBureauMemberTrue(deputy.getId());

        return new DeputyResponse(
                deputy.getId(),
                deputy.getFirstName(),
                deputy.getLastName(),
                buildDeputyFullName(deputy),
                deputy.getDeputyNumber(),
                deputy.getGender(),
                deputy.getConstituency(),
                deputy.getProvince(),
                deputy.getDistrict(),
                deputy.getPoliticalParty(),
                deputy.getMandateStatus(),
                deputy.getEmail(),
                deputy.getPhoneNumber(),
                deputy.getWhatsappNumber(),
                deputy.getPhotoUrl(),
                deputy.getSeatNumber(),
                deputy.getOfficialAddress(),
                deputy.getActive(),
                toAssemblySessionMini(deputy.getCurrentSession()),
                toParliamentaryGroupMini(deputy.getParliamentaryGroup()),
                membershipsCount,
                bureauMembershipsCount
        );
    }

    private CommitteeResponse toCommitteeResponse(Committee committee) {
        long membersCount = committee.getId() == null ? 0 : membershipRepository.countByCommitteeId(committee.getId());
        long bureauMembersCount = committee.getId() == null ? 0 : membershipRepository.countByCommitteeIdAndBureauMemberTrue(committee.getId());

        return new CommitteeResponse(
                committee.getId(),
                committee.getName(),
                committee.getCode(),
                committee.getCommitteeType(),
                committee.getThematicScope(),
                committee.getDescription(),
                committee.getRoom(),
                committee.getContactEmail(),
                committee.getContactPhone(),
                committee.getActive(),
                toAssemblySessionMini(committee.getActiveSession()),
                membersCount,
                bureauMembersCount
        );
    }

    private CommitteeMembershipResponse toCommitteeMembershipResponse(CommitteeMembership membership) {
        return new CommitteeMembershipResponse(
                membership.getId(),
                toCommitteeMini(membership.getCommittee()),
                toDeputyMini(membership.getDeputy()),
                toAssemblySessionMini(membership.getAssemblySession()),
                membership.getOfficeRole(),
                membership.getStartDate(),
                membership.getEndDate(),
                membership.getPrimaryMembership(),
                membership.getBureauMember(),
                membership.getNotes()
        );
    }

    private ParliamentaryGroupResponse toParliamentaryGroupResponse(ParliamentaryGroup group) {
        long deputiesCount = group.getId() == null ? 0 : deputyRepository.countByParliamentaryGroupId(group.getId());
        return new ParliamentaryGroupResponse(
                group.getId(),
                group.getName(),
                group.getAcronym(),
                group.getDescription(),
                group.getLeaderName(),
                group.getContactEmail(),
                group.getContactPhone(),
                group.getOfficial(),
                deputiesCount
        );
    }

    private AssemblySessionResponse toAssemblySessionResponse(AssemblySession session) {
        return new AssemblySessionResponse(
                session.getId(),
                session.getTitle(),
                session.getSessionType(),
                session.getLegislatureLabel(),
                session.getYearLabel(),
                session.getStartDate(),
                session.getEndDate(),
                session.getStatus(),
                session.getOpeningDecreeReference(),
                session.getNotes()
        );
    }

    private AssemblySessionMiniResponse toAssemblySessionMini(AssemblySession session) {
        if (session == null) {
            return null;
        }
        return new AssemblySessionMiniResponse(
                session.getId(),
                session.getTitle(),
                session.getSessionType(),
                session.getLegislatureLabel(),
                session.getYearLabel(),
                session.getStatus()
        );
    }

    private ParliamentaryGroupMiniResponse toParliamentaryGroupMini(ParliamentaryGroup group) {
        if (group == null) {
            return null;
        }
        return new ParliamentaryGroupMiniResponse(
                group.getId(),
                group.getName(),
                group.getAcronym()
        );
    }

    private DeputyMiniResponse toDeputyMini(Deputy deputy) {
        if (deputy == null) {
            return null;
        }
        return new DeputyMiniResponse(
                deputy.getId(),
                deputy.getDeputyNumber(),
                buildDeputyFullName(deputy),
                deputy.getConstituency(),
                deputy.getProvince(),
                deputy.getActive()
        );
    }

    private CommitteeMiniResponse toCommitteeMini(Committee committee) {
        if (committee == null) {
            return null;
        }
        return new CommitteeMiniResponse(
                committee.getId(),
                committee.getName(),
                committee.getCode(),
                committee.getCommitteeType(),
                committee.getActive()
        );
    }

    private String buildDeputyFullName(Deputy deputy) {
        String firstName = deputy.getFirstName() == null ? "" : deputy.getFirstName().trim();
        String lastName = deputy.getLastName() == null ? "" : deputy.getLastName().trim();
        return (firstName + " " + lastName).trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String normalizeUpper(String value, String defaultValue) {
        String trimmed = trimToNull(value);
        return trimmed == null ? defaultValue : trimmed.toUpperCase(Locale.ROOT);
    }

    private String normalizeUpperNullable(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? null : trimmed.toUpperCase(Locale.ROOT);
    }
}