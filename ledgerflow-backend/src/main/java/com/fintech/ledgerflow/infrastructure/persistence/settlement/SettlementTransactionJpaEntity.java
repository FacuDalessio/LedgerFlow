package com.fintech.ledgerflow.infrastructure.persistence.settlement;

import com.fintech.ledgerflow.domain.settlement.SettlementState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlement_transactions")
public class SettlementTransactionJpaEntity {
    @Id
    private UUID id;
    @Column(name = "external_transaction_id", nullable = false, unique = true, length = 100)
    private String externalTransactionId;
    @Column(name = "source_account", nullable = false, length = 100)
    private String sourceAccount;
    @Column(name = "destination_account", nullable = false, length = 100)
    private String destinationAccount;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(name = "fee_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal feeAmount;
    @Column(name = "transaction_timestamp", nullable = false)
    private Instant transactionTimestamp;
    @Column(name = "merchant_code", nullable = false, length = 100)
    private String merchantCode;
    @Column(name = "amount_usd", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountUsd;
    @Column(name = "fee_usd", nullable = false, precision = 19, scale = 4)
    private BigDecimal feeUsd;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private SettlementState state;

    protected SettlementTransactionJpaEntity() {
    }

    public SettlementTransactionJpaEntity(UUID id, String externalTransactionId, String sourceAccount,
                                         String destinationAccount, BigDecimal amount, String currency,
                                         BigDecimal feeAmount, Instant transactionTimestamp, String merchantCode,
                                         BigDecimal amountUsd, BigDecimal feeUsd, SettlementState state) {
        this.id = id;
        this.externalTransactionId = externalTransactionId;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.currency = currency;
        this.feeAmount = feeAmount;
        this.transactionTimestamp = transactionTimestamp;
        this.merchantCode = merchantCode;
        this.amountUsd = amountUsd;
        this.feeUsd = feeUsd;
        this.state = state;
    }

    public UUID getId() { return id; }
    public String getExternalTransactionId() { return externalTransactionId; }
}
