package com.agora.assemblee.legislation.dto;

import java.time.Instant;

public record LegislativeTextVersionResponse(
        Long id,
        Long legislativeTextId,
        Integer versionNumber,
        String versionLabel,
        String contentHash,
        Boolean currentVersion,
        Boolean publishable,
        Instant createdAt,
        Instant updatedAt
) {}