package com.fintech.ledgerflow.application.account;

import com.fintech.ledgerflow.domain.account.Account;
import com.fintech.ledgerflow.domain.account.AccountRepository;
import com.fintech.ledgerflow.domain.account.AccountStatus;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService implements AccountUseCase {

    private final AccountRepository repository;
    private final Clock clock;

    @Autowired
    AccountService(AccountRepository repository) {
        this(repository, Clock.systemUTC());
    }

    AccountService(AccountRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Account create(String accountNumber, String currency, BigDecimal balance) {
        if (repository.existsByAccountNumber(accountNumber.trim())) {
            throw new AccountConflictException(accountNumber);
        }
        try {
            return repository.save(Account.create(accountNumber, currency, balance, Instant.now(clock)));
        } catch (DataIntegrityViolationException exception) {
            throw new AccountConflictException(accountNumber);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Override
    @Transactional
    public Account update(UUID id, String currency, BigDecimal balance, AccountStatus status) {
        Account account = findById(id);
        account.update(currency, balance, status, Instant.now(clock));
        return repository.save(account);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Account account = findById(id);
        account.deactivate(Instant.now(clock));
        repository.save(account);
    }
}
