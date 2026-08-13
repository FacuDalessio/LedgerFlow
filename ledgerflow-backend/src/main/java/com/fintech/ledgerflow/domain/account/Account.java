package com.fintech.ledgerflow.domain.account;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class Account {

    private final UUID id;
    private final String accountNumber;
    private String currency;
    private BigDecimal balance;
    private AccountStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Account(UUID id, String accountNumber, String currency, BigDecimal balance,
                    AccountStatus status, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.accountNumber = requireText(accountNumber, "accountNumber");
        this.currency = validateCurrency(currency);
        this.balance = validateBalance(balance);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Account create(String accountNumber, String currency, BigDecimal balance, Instant now) {
        return new Account(UUID.randomUUID(), accountNumber, currency, balance, AccountStatus.ACTIVE, now, now);
    }

    public static Account reconstitute(UUID id, String accountNumber, String currency, BigDecimal balance,
                                       AccountStatus status, Instant createdAt, Instant updatedAt) {
        return new Account(id, accountNumber, currency, balance, status, createdAt, updatedAt);
    }

    public void update(String currency, BigDecimal balance, AccountStatus status, Instant now) {
        this.currency = validateCurrency(currency);
        this.balance = validateBalance(balance);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.updatedAt = Objects.requireNonNull(now, "now must not be null");
    }

    public void deactivate(Instant now) {
        this.status = AccountStatus.INACTIVE;
        this.updatedAt = Objects.requireNonNull(now, "now must not be null");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }

    private static String validateCurrency(String value) {
        String currency = requireText(value, "currency").toUpperCase(Locale.ROOT);
        if (!currency.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("currency must be a 3-letter code");
        }
        return currency;
    }

    private static BigDecimal validateBalance(BigDecimal value) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException("balance must not be negative");
        }
        return value;
    }

    public UUID getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
