package com.agora.assemblee.plenary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlenarySessionRequest(@NotBlank String title, String sessionType, @NotNull Long assemblySessionId, Long presidingUserId) {}
