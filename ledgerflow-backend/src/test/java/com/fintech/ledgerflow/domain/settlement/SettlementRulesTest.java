package com.fintech.ledgerflow.domain.settlement;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SettlementRulesTest {
    @Test
    void feeDiscrepancyTakesPrecedenceOverAuditState() {
        assertThat(SettlementRules.state(new BigDecimal("10000.00"), new BigDecimal("10000.00"),
                new BigDecimal("99.99"))).isEqualTo(SettlementState.FEE_DISCREPANCY);
    }

    @Test
    void marksCorrectHighValueTransactionForAudit() {
        assertThat(SettlementRules.state(new BigDecimal("10000.00"), new BigDecimal("10000.00"),
                new BigDecimal("100.00"))).isEqualTo(SettlementState.SETTLED_PENDING_AUDIT);
    }

    @Test
    void convertsWithFourDecimalPlaces() {
        assertThat(SettlementRules.convertToUsd(new BigDecimal("125.00"), new BigDecimal("1.0825")))
                .isEqualByComparingTo("135.3125");
    }
}
