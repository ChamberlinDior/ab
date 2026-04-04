package com.agora.assemblee.institution.dto;

public record DeputyResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String deputyNumber,
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
        AssemblySessionMiniResponse currentSession,
        ParliamentaryGroupMiniResponse parliamentaryGroup,
        long membershipsCount,
        long bureauMembershipsCount
) {}