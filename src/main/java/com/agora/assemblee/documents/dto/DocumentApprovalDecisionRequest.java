package com.agora.assemblee.documents.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentApprovalDecisionRequest {

    @NotBlank(message = "Le commentaire est obligatoire")
    private String comment;
}