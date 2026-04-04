package com.agora.assemblee.workflow.controller;

import com.agora.assemblee.common.api.ApiResponse;
import com.agora.assemblee.workflow.model.WorkflowInstance;
import com.agora.assemblee.workflow.repository.WorkflowInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowInstanceRepository repository;

    @GetMapping
    public ApiResponse<List<WorkflowInstance>> list() {
        return ApiResponse.ok(repository.findAll(), "Liste des workflows");
    }
}
