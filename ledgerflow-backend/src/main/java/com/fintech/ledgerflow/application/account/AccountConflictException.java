package com.fintech.ledgerflow.application.account;

public class AccountConflictException extends RuntimeException {
    public AccountConflictException(String accountNumber) {
        super("An account with accountNumber already exists: " + accountNumber);
    }
}
