package com.fintech.ledgerflow.application.account;

import com.fintech.ledgerflow.domain.account.Account;
import com.fintech.ledgerflow.domain.account.AccountStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountUseCase {
    Account create(String accountNumber, String currency, BigDecimal balance);
    List<Account> findAll();
    Account findById(UUID id);
    Account update(UUID id, String currency, BigDecimal balance, AccountStatus status);
    void deactivate(UUID id);
}
