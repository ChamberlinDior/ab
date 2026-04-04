package com.agora.assemblee.institution.dto;

public record CommitteeResponse(
        Long id,
        String name,
        String code,
        String committeeType,
        String thematicScope,
        String description,
        String room,
        String contactEmail,
        String contactPhone,
        Boolean active,
        AssemblySessionMiniResponse activeSession,
        long membersCount,
        long bureauMembersCount
) {}