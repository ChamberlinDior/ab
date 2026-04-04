package com.agora.assemblee.institution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CommitteeMembershipRequest(
        @NotNull Long committeeId,
        @NotNull Long deputyId,
        @NotNull Long assemblySessionId,
        @NotBlank String officeRole,
        LocalDate startDate,
        LocalDate endDate,
        Boolean primaryMembership,
        Boolean bureauMember,
        String notes
) {}