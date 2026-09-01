package com.fintech.ledgerflow.domain.settlement;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class SettlementRules {
    public static final BigDecimal FEE_RATE = new BigDecimal("0.01");
    public static final BigDecimal AUDIT_THRESHOLD_USD = new BigDecimal("10000.00");

    private SettlementRules() {
    }

    public static SettlementState state(BigDecimal amountUsd, BigDecimal amount, BigDecimal feeAmount) {
        if (feeAmount.compareTo(amount.multiply(FEE_RATE)) != 0) {
            return SettlementState.FEE_DISCREPANCY;
        }
        return amountUsd.compareTo(AUDIT_THRESHOLD_USD) >= 0
                ? SettlementState.SETTLED_PENDING_AUDIT
                : SettlementState.SETTLED_APPROVED;
    }

    public static BigDecimal convertToUsd(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(4, RoundingMode.HALF_UP);
    }
}
