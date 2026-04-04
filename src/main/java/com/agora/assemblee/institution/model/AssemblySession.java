package com.agora.assemblee.institution.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class AssemblySession extends BaseEntity {

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 80)
    private String sessionType;

    @Column(length = 120)
    private String legislatureLabel;

    @Column(nullable = false)
    private Integer yearLabel;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 40)
    private String status = "ACTIVE";

    @Column(length = 150)
    private String openingDecreeReference;

    @Column(length = 1200)
    private String notes;
}