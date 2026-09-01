package com.fintech.ledgerflow.domain.settlement;

import java.math.BigDecimal;
import java.time.Instant;

public record SettlementTransactionInput(
        String externalTransactionId, String sourceAccount, String destinationAccount,
        BigDecimal amount, String currency, BigDecimal feeAmount, Instant transactionTimestamp,
        String merchantCode) {
}
