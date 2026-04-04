package com.agora.assemblee.institution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeputyRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String deputyNumber,
        String gender,
        String constituency,
        String province,
        String district,
        String politicalParty,
        String mandateStatus,
        String email,
        String phoneNumber,
        String whatsappNumber,
        String photoUrl,
        String seatNumber,
        String officialAddress,
        Boolean active,
        Long parliamentaryGroupId,
        @NotNull Long currentSessionId
) {}