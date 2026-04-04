package com.agora.assemblee.common.api;

import java.time.Instant;
import java.util.Map;

public record ApiError(String code, String message, Map<String, String> details, Instant timestamp) {}
