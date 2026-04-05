package com.agora.assemblee.plenary.dto;

import java.time.LocalDateTime;

public record PlenarySessionResponse(
        Long id,
        String title,
        String sessionType,
        Long assemblySessionId,
        String assemblySessionLabel,
        Long presidingUserId,
        String presidingUserName,
        String location,
        String status,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Integer expectedMembersCount,
        Integer presentMembersCount,
        Integer quorumThresholdPercent,
        Boolean quorumReached,
        Integer agendaItemsCount
) {}