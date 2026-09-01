package com.fintech.ledgerflow.domain.settlement;

public interface SettlementTransactionRepository {
    SettlementTransaction save(SettlementTransaction transaction);

    boolean existsByExternalTransactionId(String externalTransactionId);
}
