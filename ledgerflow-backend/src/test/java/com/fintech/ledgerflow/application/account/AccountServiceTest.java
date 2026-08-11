package com.fintech.ledgerflow.application.account;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fintech.ledgerflow.domain.account.Account;
import com.fintech.ledgerflow.domain.account.AccountRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    private AccountService service;

    @BeforeEach
    void setUp() {
        service = new AccountService(repository, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
    }

    @Test
    void rejectsDuplicateAccountNumber() {
        when(repository.existsByAccountNumber("ACC-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create("ACC-001", "USD", BigDecimal.ZERO))
                .isInstanceOf(AccountConflictException.class);
    }

    @Test
    void deactivatesAndPersistsAnExistingAccount() {
        UUID id = UUID.randomUUID();
        Account account = Account.reconstitute(id, "ACC-001", "USD", BigDecimal.ZERO,
                com.fintech.ledgerflow.domain.account.AccountStatus.ACTIVE, Instant.EPOCH, Instant.EPOCH);
        when(repository.findById(id)).thenReturn(Optional.of(account));

        service.deactivate(id);

        verify(repository).save(account);
    }
}
