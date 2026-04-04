package com.agora.assemblee.dashboard.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.documents.repository.DocumentRepository;
import com.agora.assemblee.legislation.repository.LegislativeTextRepository;
import com.agora.assemblee.plenary.repository.PlenarySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final LegislativeTextRepository textRepository;
    private final DocumentRepository documentRepository;
    private final PlenarySessionRepository plenarySessionRepository;

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        return ApiResponse.ok(Map.of(
                "texts", textRepository.count(),
                "documents", documentRepository.count(),
                "plenarySessions", plenarySessionRepository.count()
        ), "Synthèse du tableau de bord");
    }
}
