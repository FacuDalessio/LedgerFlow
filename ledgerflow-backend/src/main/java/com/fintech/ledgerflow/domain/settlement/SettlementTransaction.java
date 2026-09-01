package com.fintech.ledgerflow.domain.settlement;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SettlementTransaction(
        UUID id, String externalTransactionId, String sourceAccount, String destinationAccount,
        BigDecimal amount, String currency, BigDecimal feeAmount, Instant transactionTimestamp,
        String merchantCode, BigDecimal amountUsd, BigDecimal feeUsd, SettlementState state) {

    public SettlementTransaction {
        Objects.requireNonNull(id, "id must not be null");
        requireText(externalTransactionId, "externalTransactionId");
        requireText(sourceAccount, "sourceAccount");
        requireText(destinationAccount, "destinationAccount");
        requireText(currency, "currency");
        requireText(merchantCode, "merchantCode");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(feeAmount, "feeAmount must not be null");
        Objects.requireNonNull(transactionTimestamp, "transactionTimestamp must not be null");
        Objects.requireNonNull(amountUsd, "amountUsd must not be null");
        Objects.requireNonNull(feeUsd, "feeUsd must not be null");
        Objects.requireNonNull(state, "state must not be null");
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
