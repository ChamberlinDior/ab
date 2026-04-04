package com.agora.assemblee.citizen.controller;

import com.agora.assemblee.citizen.model.PublicConsultation;
import com.agora.assemblee.citizen.repository.PublicConsultationRepository;
import com.agora.assemblee.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citizen")
@RequiredArgsConstructor
public class CitizenController {
    private final PublicConsultationRepository repository;

    @GetMapping("/public/consultations")
    public ApiResponse<List<PublicConsultation>> listPublicConsultations() {
        return ApiResponse.ok(repository.findAll(), "Consultations publiques");
    }
}
