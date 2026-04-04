package com.agora.assemblee.institution.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.institution.dto.AssemblySessionRequest;
import com.agora.assemblee.institution.dto.AssemblySessionResponse;
import com.agora.assemblee.institution.dto.CommitteeMembershipRequest;
import com.agora.assemblee.institution.dto.CommitteeMembershipResponse;
import com.agora.assemblee.institution.dto.CommitteeRequest;
import com.agora.assemblee.institution.dto.CommitteeResponse;
import com.agora.assemblee.institution.dto.DeputyRequest;
import com.agora.assemblee.institution.dto.DeputyResponse;
import com.agora.assemblee.institution.dto.InstitutionSummaryResponse;
import com.agora.assemblee.institution.dto.ParliamentaryGroupRequest;
import com.agora.assemblee.institution.dto.ParliamentaryGroupResponse;
import com.agora.assemblee.institution.service.InstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/institution", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InstitutionController {

    private final InstitutionService service;

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(
                Map.of(
                        "module", "institution",
                        "status", "UP",
                        "message", "Le module Institution est bien chargé"
                ),
                "Santé du module Institution"
        );
    }

    @GetMapping("/summary")
    public ApiResponse<InstitutionSummaryResponse> getSummary() {
        return ApiResponse.ok(service.getSummary(), "Synthèse institutionnelle");
    }

    @PostMapping(value = "/assembly-sessions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AssemblySessionResponse> createAssemblySession(
            @Valid @RequestBody AssemblySessionRequest request
    ) {
        return ApiResponse.ok(service.createAssemblySession(request), "Session créée");
    }

    @GetMapping("/assembly-sessions")
    public ApiResponse<List<AssemblySessionResponse>> listAssemblySessions() {
        return ApiResponse.ok(service.listAssemblySessions(), "Liste des sessions");
    }

    @GetMapping("/assembly-sessions/active")
    public ApiResponse<AssemblySessionResponse> getActiveAssemblySession() {
        return ApiResponse.ok(service.getActiveAssemblySession(), "Session active");
    }

    @PostMapping(value = "/parliamentary-groups", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ParliamentaryGroupResponse> createParliamentaryGroup(
            @Valid @RequestBody ParliamentaryGroupRequest request
    ) {
        return ApiResponse.ok(service.createParliamentaryGroup(request), "Groupe parlementaire créé");
    }

    @GetMapping("/parliamentary-groups")
    public ApiResponse<List<ParliamentaryGroupResponse>> listParliamentaryGroups() {
        return ApiResponse.ok(service.listParliamentaryGroups(), "Liste des groupes parlementaires");
    }

    @PostMapping(value = "/deputies", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<DeputyResponse> createDeputy(@Valid @RequestBody DeputyRequest request) {
        return ApiResponse.ok(service.createDeputy(request), "Député créé");
    }

    @GetMapping("/deputies")
    public ApiResponse<List<DeputyResponse>> listDeputies(
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) Long sessionId
    ) {
        return ApiResponse.ok(service.listDeputies(activeOnly, sessionId), "Liste des députés");
    }

    @GetMapping("/deputies/search")
    public ApiResponse<List<DeputyResponse>> searchDeputies(
            @RequestParam String keyword,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) Long sessionId
    ) {
        return ApiResponse.ok(service.searchDeputies(keyword, activeOnly, sessionId), "Résultats de recherche");
    }

    @GetMapping("/deputies/{deputyId}")
    public ApiResponse<DeputyResponse> getDeputy(@PathVariable Long deputyId) {
        return ApiResponse.ok(service.getDeputy(deputyId), "Fiche député");
    }

    @GetMapping("/deputies/{deputyId}/memberships")
    public ApiResponse<List<CommitteeMembershipResponse>> listDeputyMemberships(@PathVariable Long deputyId) {
        return ApiResponse.ok(service.listDeputyMemberships(deputyId), "Affectations du député");
    }

    @PostMapping(value = "/committees", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CommitteeResponse> createCommittee(@Valid @RequestBody CommitteeRequest request) {
        return ApiResponse.ok(service.createCommittee(request), "Commission créée");
    }

    @GetMapping("/committees")
    public ApiResponse<List<CommitteeResponse>> listCommittees(
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Boolean activeOnly
    ) {
        return ApiResponse.ok(service.listCommittees(sessionId, activeOnly), "Liste des commissions");
    }

    @GetMapping("/committees/{committeeId}")
    public ApiResponse<CommitteeResponse> getCommittee(@PathVariable Long committeeId) {
        return ApiResponse.ok(service.getCommittee(committeeId), "Détail commission");
    }

    @PostMapping(value = "/committee-memberships", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CommitteeMembershipResponse> assignDeputyToCommittee(
            @Valid @RequestBody CommitteeMembershipRequest request
    ) {
        return ApiResponse.ok(service.assignDeputyToCommittee(request), "Affectation réalisée");
    }

    @GetMapping("/committees/{committeeId}/memberships")
    public ApiResponse<List<CommitteeMembershipResponse>> listCommitteeMemberships(@PathVariable Long committeeId) {
        return ApiResponse.ok(service.listCommitteeMemberships(committeeId), "Membres de la commission");
    }

    @GetMapping("/committees/{committeeId}/bureau")
    public ApiResponse<List<CommitteeMembershipResponse>> listCommitteeBureau(@PathVariable Long committeeId) {
        return ApiResponse.ok(service.listCommitteeBureau(committeeId), "Bureau de la commission");
    }
}