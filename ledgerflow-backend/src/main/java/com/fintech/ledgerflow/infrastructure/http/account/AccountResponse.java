package com.fintech.ledgerflow.infrastructure.http.account;

import com.fintech.ledgerflow.domain.account.Account;
import com.fintech.ledgerflow.domain.account.AccountStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(UUID id, String accountNumber, String currency, BigDecimal balance,
                              AccountStatus status, Instant createdAt, Instant updatedAt) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getCurrency(),
                account.getBalance(), account.getStatus(), account.getCreatedAt(), account.getUpdatedAt());
    }
}
