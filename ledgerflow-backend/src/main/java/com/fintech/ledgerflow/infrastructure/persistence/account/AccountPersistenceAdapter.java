package com.fintech.ledgerflow.infrastructure.persistence.account;

import com.fintech.ledgerflow.domain.account.Account;
import com.fintech.ledgerflow.domain.account.AccountRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AccountPersistenceAdapter implements AccountRepository {

    private final SpringDataAccountRepository repository;

    public AccountPersistenceAdapter(SpringDataAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account save(Account account) {
        return toDomain(repository.save(toEntity(account)));
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return repository.findById(id).map(AccountPersistenceAdapter::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return repository.findAllByOrderByAccountNumberAsc().stream()
                .map(AccountPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber).map(AccountPersistenceAdapter::toDomain);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return repository.existsByAccountNumber(accountNumber);
    }

    private static AccountJpaEntity toEntity(Account account) {
        return new AccountJpaEntity(account.getId(), account.getAccountNumber(), account.getCurrency(),
                account.getBalance(), account.getStatus(), account.getCreatedAt(), account.getUpdatedAt());
    }

    private static Account toDomain(AccountJpaEntity entity) {
        return Account.reconstitute(entity.getId(), entity.getAccountNumber(), entity.getCurrency(),
                entity.getBalance(), entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
