package com.fintech.ledgerflow.application.settlement;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateProvider;
import com.fintech.ledgerflow.domain.account.Account;
import com.fintech.ledgerflow.domain.account.AccountRepository;
import com.fintech.ledgerflow.domain.account.AccountStatus;
import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import com.fintech.ledgerflow.domain.settlement.SettlementRules;
import com.fintech.ledgerflow.domain.settlement.SettlementTransaction;
import com.fintech.ledgerflow.domain.settlement.SettlementTransactionInput;
import com.fintech.ledgerflow.domain.settlement.SettlementTransactionRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
public final class SettlementItemProcessor {
    private final AccountRepository accountRepository;
    private final SettlementTransactionRepository transactionRepository;
    private final ExchangeRateProvider exchangeRateProvider;
    private final SettlementExchangeRateCache exchangeRateCache;
    private final Map<String, SettlementTransactionInput> seenInputsByExternalTransactionId = new HashMap<>();

    public SettlementItemProcessor(AccountRepository accountRepository,
                                   SettlementTransactionRepository transactionRepository,
                                   ExchangeRateProvider exchangeRateProvider,
                                   SettlementExchangeRateCache exchangeRateCache) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.exchangeRateProvider = exchangeRateProvider;
        this.exchangeRateCache = exchangeRateCache;
    }

    public SettlementTransaction process(SettlementTransactionInput input) {
        validate(input);
        SettlementTransactionInput firstInput = seenInputsByExternalTransactionId.putIfAbsent(
                input.externalTransactionId(), input);
        if ((firstInput != null && firstInput != input)
                || (firstInput == null
                && transactionRepository.existsByExternalTransactionId(input.externalTransactionId()))) {
            throw new InvalidSettlementTransactionException("Duplicate external transaction ID: "
                    + input.externalTransactionId());
        }
        requireActiveAccount(input.sourceAccount());
        requireActiveAccount(input.destinationAccount());

        BigDecimal rate = input.currency().equalsIgnoreCase("USD")
                ? BigDecimal.ONE
                : exchangeRate(input.currency()).rate();
        BigDecimal amountUsd = SettlementRules.convertToUsd(input.amount(), rate);
        BigDecimal feeUsd = SettlementRules.convertToUsd(input.feeAmount(), rate);
        return new SettlementTransaction(UUID.randomUUID(), input.externalTransactionId(), input.sourceAccount(),
                input.destinationAccount(), input.amount(), input.currency().toUpperCase(Locale.ROOT), input.feeAmount(),
                input.transactionTimestamp(), input.merchantCode(), amountUsd, feeUsd,
                SettlementRules.state(amountUsd, input.amount(), input.feeAmount()));
    }

    private ExchangeRate exchangeRate(String currency) {
        return exchangeRateCache.getOrFetch(currency.toUpperCase(Locale.ROOT), exchangeRateProvider);
    }

    private void requireActiveAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new InvalidSettlementTransactionException(
                        "Account does not exist: " + accountNumber));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidSettlementTransactionException("Account is inactive: " + accountNumber);
        }
    }

    private static void validate(SettlementTransactionInput input) {
        if (input == null || input.externalTransactionId() == null || input.sourceAccount() == null
                || input.destinationAccount() == null || input.amount() == null || input.currency() == null
                || input.feeAmount() == null || input.transactionTimestamp() == null || input.merchantCode() == null) {
            throw new InvalidSettlementTransactionException("Settlement record must not contain null fields");
        }
        if (input.amount().signum() <= 0) {
            throw new InvalidSettlementTransactionException("Amount must be greater than zero");
        }
        if (input.feeAmount().signum() < 0) {
            throw new InvalidSettlementTransactionException("Fee amount must not be negative");
        }
    }
}
