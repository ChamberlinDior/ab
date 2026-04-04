package com.agora.assemblee.institution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AssemblySessionRequest(
        @NotBlank String title,
        @NotBlank String sessionType,
        String legislatureLabel,
        @NotNull Integer yearLabel,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        String status,
        String openingDecreeReference,
        String notes
) {}