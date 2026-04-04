package com.agora.assemblee.plenary.service;

import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.auth.repository.UserRepository;
import com.agora.assemblee.common.enums.VoteChoice;
import com.agora.assemblee.common.exception.ResourceNotFoundException;
import com.agora.assemblee.institution.model.AssemblySession;
import com.agora.assemblee.institution.model.Deputy;
import com.agora.assemblee.institution.repository.AssemblySessionRepository;
import com.agora.assemblee.institution.repository.DeputyRepository;
import com.agora.assemblee.plenary.dto.PlenarySessionRequest;
import com.agora.assemblee.plenary.model.*;
import com.agora.assemblee.plenary.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlenaryService {
    private final PlenarySessionRepository plenarySessionRepository;
    private final AssemblySessionRepository assemblySessionRepository;
    private final UserRepository userRepository;
    private final VoteSummaryRepository voteSummaryRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final DeputyRepository deputyRepository;

    @Transactional
    public PlenarySession createSession(PlenarySessionRequest request) {
        AssemblySession assemblySession = assemblySessionRepository.findById(request.assemblySessionId()).orElseThrow(() -> new ResourceNotFoundException("Session assemblée introuvable"));
        PlenarySession session = new PlenarySession();
        session.setTitle(request.title());
        session.setSessionType(request.sessionType());
        session.setAssemblySession(assemblySession);
        if (request.presidingUserId() != null) {
            User user = userRepository.findById(request.presidingUserId()).orElseThrow(() -> new ResourceNotFoundException("Président de séance introuvable"));
            session.setPresidingUser(user);
        }
        return plenarySessionRepository.save(session);
    }

    @Transactional
    public VoteRecord recordVote(Long voteSummaryId, Long deputyId, VoteChoice choice) {
        VoteSummary summary = voteSummaryRepository.findById(voteSummaryId).orElseThrow(() -> new ResourceNotFoundException("Résumé de vote introuvable"));
        Deputy deputy = deputyRepository.findById(deputyId).orElseThrow(() -> new ResourceNotFoundException("Député introuvable"));
        VoteRecord record = new VoteRecord();
        record.setVoteSummary(summary);
        record.setDeputy(deputy);
        record.setVoteChoice(choice);
        VoteRecord saved = voteRecordRepository.save(record);
        switch (choice) {
            case FOR -> summary.setVotesForCount(summary.getVotesForCount() + 1);
            case AGAINST -> summary.setVotesAgainstCount(summary.getVotesAgainstCount() + 1);
            case ABSTAIN -> summary.setAbstentionCount(summary.getAbstentionCount() + 1);
            case ABSENT -> summary.setAbsentCount(summary.getAbsentCount() + 1);
        }
        summary.setAdopted(summary.getVotesForCount() > summary.getVotesAgainstCount());
        voteSummaryRepository.save(summary);
        return saved;
    }

    public List<PlenarySession> listSessions() { return plenarySessionRepository.findAll(); }
}
