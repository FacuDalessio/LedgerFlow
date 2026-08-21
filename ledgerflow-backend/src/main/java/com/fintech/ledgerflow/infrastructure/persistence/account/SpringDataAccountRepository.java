package com.fintech.ledgerflow.infrastructure.persistence.account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {
    boolean existsByAccountNumber(String accountNumber);
    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);
    List<AccountJpaEntity> findAllByOrderByAccountNumberAsc();
}
