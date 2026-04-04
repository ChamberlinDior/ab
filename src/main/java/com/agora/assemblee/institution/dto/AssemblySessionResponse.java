package com.agora.assemblee.institution.dto;

import java.time.LocalDate;

public record AssemblySessionResponse(
        Long id,
        String title,
        String sessionType,
        String legislatureLabel,
        Integer yearLabel,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String openingDecreeReference,
        String notes
) {}