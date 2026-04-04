package com.agora.assemblee.plenary.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.common.enums.VoteChoice;
import com.agora.assemblee.plenary.dto.PlenarySessionRequest;
import com.agora.assemblee.plenary.model.PlenarySession;
import com.agora.assemblee.plenary.model.VoteRecord;
import com.agora.assemblee.plenary.service.PlenaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plenary")
@RequiredArgsConstructor
public class PlenaryController {
    private final PlenaryService service;

    @PostMapping("/sessions")
    public ApiResponse<PlenarySession> createSession(@Valid @RequestBody PlenarySessionRequest request) {
        return ApiResponse.ok(service.createSession(request), "Séance créée");
    }

    @PostMapping("/vote-summaries/{voteSummaryId}/votes")
    public ApiResponse<VoteRecord> recordVote(@PathVariable Long voteSummaryId, @RequestParam Long deputyId, @RequestParam VoteChoice choice) {
        return ApiResponse.ok(service.recordVote(voteSummaryId, deputyId, choice), "Vote enregistré");
    }

    @GetMapping("/sessions")
    public ApiResponse<List<PlenarySession>> listSessions() {
        return ApiResponse.ok(service.listSessions(), "Liste des séances");
    }
}
