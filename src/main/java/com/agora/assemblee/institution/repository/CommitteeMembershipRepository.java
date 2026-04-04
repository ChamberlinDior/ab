package com.agora.assemblee.institution.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.institution.model.CommitteeMembership;

import java.util.List;

public interface CommitteeMembershipRepository extends BaseRepository<CommitteeMembership> {

    List<CommitteeMembership> findByCommitteeIdOrderByPrimaryMembershipDescBureauMemberDescOfficeRoleAscDeputyLastNameAscDeputyFirstNameAsc(Long committeeId);

    List<CommitteeMembership> findByDeputyIdOrderByStartDateDescIdDesc(Long deputyId);

    List<CommitteeMembership> findByCommitteeIdAndBureauMemberTrueOrderByOfficeRoleAscDeputyLastNameAscDeputyFirstNameAsc(Long committeeId);

    long countByCommitteeId(Long committeeId);

    long countByCommitteeIdAndBureauMemberTrue(Long committeeId);

    long countByDeputyId(Long deputyId);

    long countByDeputyIdAndBureauMemberTrue(Long deputyId);

    boolean existsByCommitteeIdAndDeputyIdAndAssemblySessionId(Long committeeId, Long deputyId, Long assemblySessionId);
}