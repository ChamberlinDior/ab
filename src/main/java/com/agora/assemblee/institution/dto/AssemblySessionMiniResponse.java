package com.agora.assemblee.institution.dto;

public record AssemblySessionMiniResponse(
        Long id,
        String title,
        String sessionType,
        String legislatureLabel,
        Integer yearLabel,
        String status
) {}