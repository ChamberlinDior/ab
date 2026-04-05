package com.agora.assemblee.plenary.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.plenary.dto.*;
import com.agora.assemblee.plenary.service.PlenaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plenary")
@RequiredArgsConstructor
public class PlenaryController {

    private final PlenaryService service;

    @PostMapping("/sessions")
    public ApiResponse<PlenarySessionResponse> createSession(@Valid @RequestBody PlenarySessionRequest request) {
        return ApiResponse.ok(service.createSession(request), "Séance plénière créée avec succès");
    }

    @PutMapping("/sessions/{sessionId}")
    public ApiResponse<PlenarySessionResponse> updateSession(@PathVariable Long sessionId,
                                                             @Valid @RequestBody UpdatePlenarySessionRequest request) {
        return ApiResponse.ok(service.updateSession(sessionId, request), "Séance plénière mise à jour avec succès");
    }

    @PostMapping("/sessions/{sessionId}/open")
    public ApiResponse<PlenarySessionResponse> openSession(@PathVariable Long sessionId) {
        return ApiResponse.ok(service.openSession(sessionId), "Séance ouverte avec succès");
    }

    @PostMapping("/sessions/{sessionId}/close")
    public ApiResponse<PlenarySessionResponse> closeSession(@PathVariable Long sessionId,
                                                            @Valid @RequestBody CloseSessionRequest request) {
        return ApiResponse.ok(service.closeSession(sessionId, request), "Séance clôturée avec succès");
    }

    @GetMapping("/sessions/{sessionId}")
    public ApiResponse<PlenarySessionDetailResponse> getSessionDetail(@PathVariable Long sessionId) {
        return ApiResponse.ok(service.getSessionDetail(sessionId), "Détail de la séance");
    }

    @GetMapping("/sessions")
    public ApiResponse<Page<PlenarySessionResponse>> listSessions(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "20") int size,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) String status) {
        return ApiResponse.ok(service.listSessions(page, size, keyword, status), "Liste paginée des séances");
    }

    @PostMapping("/sessions/{sessionId}/agenda-items")
    public ApiResponse<AgendaItemResponse> addAgendaItem(@PathVariable Long sessionId,
                                                         @Valid @RequestBody AgendaItemRequest request) {
        return ApiResponse.ok(service.addAgendaItem(sessionId, request), "Point de l’ordre du jour ajouté");
    }

    @PutMapping("/agenda-items/{agendaItemId}")
    public ApiResponse<AgendaItemResponse> updateAgendaItem(@PathVariable Long agendaItemId,
                                                            @Valid @RequestBody UpdateAgendaItemRequest request) {
        return ApiResponse.ok(service.updateAgendaItem(agendaItemId, request), "Point de l’ordre du jour mis à jour");
    }

    @GetMapping("/sessions/{sessionId}/agenda-items")
    public ApiResponse<java.util.List<AgendaItemResponse>> listAgendaItems(@PathVariable Long sessionId) {
        return ApiResponse.ok(service.listAgendaItems(sessionId), "Liste des points de l’ordre du jour");
    }

    @PostMapping("/sessions/{sessionId}/attendances")
    public ApiResponse<SessionAttendanceResponse> recordAttendance(@PathVariable Long sessionId,
                                                                   @Valid @RequestBody SessionAttendanceRequest request) {
        return ApiResponse.ok(service.recordAttendance(sessionId, request), "Présence enregistrée");
    }

    @GetMapping("/sessions/{sessionId}/attendances")
    public ApiResponse<java.util.List<SessionAttendanceResponse>> listAttendances(@PathVariable Long sessionId) {
        return ApiResponse.ok(service.listAttendances(sessionId), "Liste des présences");
    }

    @GetMapping("/sessions/{sessionId}/quorum")
    public ApiResponse<QuorumResponse> getQuorum(@PathVariable Long sessionId) {
        return ApiResponse.ok(service.getQuorum(sessionId), "État du quorum");
    }

    @PostMapping("/agenda-items/{agendaItemId}/vote-summaries")
    public ApiResponse<VoteSummaryResponse> createVoteSummary(@PathVariable Long agendaItemId,
                                                              @Valid @RequestBody VoteSummaryRequest request) {
        return ApiResponse.ok(service.createVoteSummary(agendaItemId, request), "Résumé de vote créé");
    }

    @PostMapping("/vote-summaries/{voteSummaryId}/votes")
    public ApiResponse<VoteRecordResponse> recordVote(@PathVariable Long voteSummaryId,
                                                      @Valid @RequestBody VoteRecordRequest request) {
        return ApiResponse.ok(service.recordVote(voteSummaryId, request), "Vote enregistré avec succès");
    }

    @GetMapping("/vote-summaries/{voteSummaryId}")
    public ApiResponse<VoteSummaryResponse> getVoteSummary(@PathVariable Long voteSummaryId) {
        return ApiResponse.ok(service.getVoteSummary(voteSummaryId), "Résumé de vote");
    }

    @GetMapping("/sessions/{sessionId}/vote-summaries")
    public ApiResponse<java.util.List<VoteSummaryResponse>> listVoteSummariesBySession(@PathVariable Long sessionId) {
        return ApiResponse.ok(service.listVoteSummariesBySession(sessionId), "Liste des résumés de vote de la séance");
    }
}