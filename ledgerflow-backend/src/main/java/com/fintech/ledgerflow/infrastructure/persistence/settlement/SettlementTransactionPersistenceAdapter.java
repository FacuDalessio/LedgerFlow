package com.fintech.ledgerflow.infrastructure.persistence.settlement;

import com.fintech.ledgerflow.domain.settlement.SettlementTransaction;
import com.fintech.ledgerflow.domain.settlement.SettlementTransactionRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SettlementTransactionPersistenceAdapter implements SettlementTransactionRepository {
    private final SpringDataSettlementTransactionRepository repository;

    public SettlementTransactionPersistenceAdapter(SpringDataSettlementTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public SettlementTransaction save(SettlementTransaction transaction) {
        repository.save(toEntity(transaction));
        return transaction;
    }

    @Override
    public boolean existsByExternalTransactionId(String externalTransactionId) {
        return repository.existsByExternalTransactionId(externalTransactionId);
    }

    private static SettlementTransactionJpaEntity toEntity(SettlementTransaction transaction) {
        return new SettlementTransactionJpaEntity(transaction.id(), transaction.externalTransactionId(),
                transaction.sourceAccount(), transaction.destinationAccount(), transaction.amount(),
                transaction.currency(), transaction.feeAmount(), transaction.transactionTimestamp(),
                transaction.merchantCode(), transaction.amountUsd(), transaction.feeUsd(), transaction.state());
    }
}
