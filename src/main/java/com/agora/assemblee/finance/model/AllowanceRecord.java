package com.agora.assemblee.finance.model;

import com.agora.assemblee.common.model.BaseEntity;
import com.agora.assemblee.institution.model.Deputy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class AllowanceRecord extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Deputy deputy;
    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentBatch paymentBatch;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal grossAmount;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal deductionAmount = BigDecimal.ZERO;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal netAmount;
    private String allowanceType;
}
