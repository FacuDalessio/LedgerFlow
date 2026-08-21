package com.fintech.ledgerflow.infrastructure.persistence.settlement;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataSettlementTransactionRepository
        extends JpaRepository<SettlementTransactionJpaEntity, UUID> {
    boolean existsByExternalTransactionId(String externalTransactionId);
}
