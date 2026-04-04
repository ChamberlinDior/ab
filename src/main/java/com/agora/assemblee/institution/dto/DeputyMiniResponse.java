package com.agora.assemblee.institution.dto;

public record DeputyMiniResponse(
        Long id,
        String deputyNumber,
        String fullName,
        String constituency,
        String province,
        Boolean active
) {}