package com.agora.assemblee.finance.model;

import com.agora.assemblee.common.model.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
@Entity
public class PaymentBatch extends BaseEntity {
    private String label;
    private String status;
    private YearMonth accountingPeriod;
}
