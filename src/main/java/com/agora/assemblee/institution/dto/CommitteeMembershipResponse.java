package com.agora.assemblee.institution.dto;

import java.time.LocalDate;

public record CommitteeMembershipResponse(
        Long id,
        CommitteeMiniResponse committee,
        DeputyMiniResponse deputy,
        AssemblySessionMiniResponse assemblySession,
        String officeRole,
        LocalDate startDate,
        LocalDate endDate,
        Boolean primaryMembership,
        Boolean bureauMember,
        String notes
) {}