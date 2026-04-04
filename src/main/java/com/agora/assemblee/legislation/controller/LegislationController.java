package com.agora.assemblee.legislation.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.common.enums.LegislativeTextStatus;
import com.agora.assemblee.legislation.dto.*;
import com.agora.assemblee.legislation.service.LegislationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/legislation")
@RequiredArgsConstructor
public class LegislationController {

    private final LegislationService service;

    @PostMapping("/texts")
    public ApiResponse<LegislativeTextResponse> createText(@Valid @RequestBody LegislativeTextRequest request) {
        return ApiResponse.ok(service.createText(request), "Texte législatif créé avec succès");
    }

    @GetMapping("/texts")
    public ApiResponse<List<LegislativeTextResponse>> listTexts() {
        return ApiResponse.ok(service.listTexts(), "Liste des textes législatifs");
    }

    @GetMapping("/texts/{id}")
    public ApiResponse<LegislativeTextResponse> getText(@PathVariable Long id) {
        return ApiResponse.ok(service.getText(id), "Détail du texte législatif");
    }

    @GetMapping("/texts/search")
    public ApiResponse<List<LegislativeTextResponse>> searchTexts(@RequestParam String keyword) {
        return ApiResponse.ok(service.searchTexts(keyword), "Résultat de recherche des textes");
    }

    @PostMapping("/texts/{id}/status")
    public ApiResponse<LegislativeTextResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam LegislativeTextStatus status
    ) {
        return ApiResponse.ok(service.updateWorkflowStatus(id, status), "Statut du texte mis à jour");
    }

    @PostMapping("/versions")
    public ApiResponse<LegislativeTextVersionResponse> createVersion(@Valid @RequestBody VersionRequest request) {
        return ApiResponse.ok(service.createVersion(request), "Version créée avec succès");
    }

    @GetMapping("/texts/{id}/versions")
    public ApiResponse<List<LegislativeTextVersionResponse>> listVersions(@PathVariable Long id) {
        return ApiResponse.ok(service.listVersionsByText(id), "Liste des versions du texte");
    }

    @PostMapping("/assignments")
    public ApiResponse<LegislativeTextResponse> assignText(@Valid @RequestBody TextAssignmentRequest request) {
        return ApiResponse.ok(service.assignText(request), "Texte affecté avec succès");
    }

    @PostMapping("/amendments")
    public ApiResponse<AmendmentResponse> createAmendment(@Valid @RequestBody AmendmentRequest request) {
        return ApiResponse.ok(service.createAmendment(request), "Amendement créé avec succès");
    }

    @GetMapping("/texts/{id}/amendments")
    public ApiResponse<List<AmendmentResponse>> listAmendments(@PathVariable Long id) {
        return ApiResponse.ok(service.listAmendmentsByText(id), "Liste des amendements du texte");
    }

    @PostMapping("/reports")
    public ApiResponse<CommissionReportResponse> createCommissionReport(@Valid @RequestBody CommissionReportRequest request) {
        return ApiResponse.ok(service.createCommissionReport(request), "Rapport de commission créé avec succès");
    }

    @GetMapping("/texts/{id}/reports")
    public ApiResponse<List<CommissionReportResponse>> listReports(@PathVariable Long id) {
        return ApiResponse.ok(service.listReportsByText(id), "Liste des rapports de commission");
    }

    @GetMapping("/dashboard")
    public ApiResponse<LegislationDashboardResponse> dashboard() {
        return ApiResponse.ok(service.getDashboard(), "Indicateurs du module législation");
    }
}