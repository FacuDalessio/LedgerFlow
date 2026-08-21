package com.fintech.ledgerflow.application.settlement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateProvider;
import com.fintech.ledgerflow.domain.account.Account;
import com.fintech.ledgerflow.domain.account.AccountRepository;
import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import com.fintech.ledgerflow.domain.settlement.SettlementState;
import com.fintech.ledgerflow.domain.settlement.SettlementTransactionInput;
import com.fintech.ledgerflow.domain.settlement.SettlementTransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettlementItemProcessorTest {
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final SettlementTransactionRepository transactionRepository = mock(SettlementTransactionRepository.class);
    private final ExchangeRateProvider exchangeRateProvider = mock(ExchangeRateProvider.class);
    private SettlementItemProcessor processor;

    @BeforeEach
    void setUp() {
        Account source = Account.create("ACC-SOURCE", "USD", BigDecimal.ZERO, Instant.EPOCH);
        Account destination = Account.create("ACC-DEST", "USD", BigDecimal.ZERO, Instant.EPOCH);
        when(accountRepository.findByAccountNumber("ACC-SOURCE")).thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("ACC-DEST")).thenReturn(Optional.of(destination));
        when(transactionRepository.existsByExternalTransactionId("TX-1")).thenReturn(false);
        when(exchangeRateProvider.fetch("EUR"))
                .thenReturn(new ExchangeRate(LocalDate.of(2026, 8, 21), "EUR", "USD", new BigDecimal("1.08")));
        processor = new SettlementItemProcessor(accountRepository, transactionRepository, exchangeRateProvider,
                new SettlementExchangeRateCache());
    }

    @Test
    void validatesAccountsConvertsAmountsAndUsesTheAuditThresholdInUsd() {
        var result = processor.process(input(new BigDecimal("10000.00"), "EUR", new BigDecimal("100.00")));

        assertThat(result.amountUsd()).isEqualByComparingTo("10800.0000");
        assertThat(result.feeUsd()).isEqualByComparingTo("108.0000");
        assertThat(result.state()).isEqualTo(SettlementState.SETTLED_PENDING_AUDIT);
    }

    @Test
    void rejectsInactiveOrMissingAccounts() {
        when(accountRepository.findByAccountNumber("ACC-SOURCE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processor.process(input(new BigDecimal("10.00"), "USD", new BigDecimal("0.10"))))
                .isInstanceOf(InvalidSettlementTransactionException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void rejectsDuplicateExternalTransactionIdsWithinTheSameJob() {
        processor.process(input(new BigDecimal("10.00"), "USD", new BigDecimal("0.10")));

        assertThatThrownBy(() -> processor.process(input(new BigDecimal("10.00"), "USD", new BigDecimal("0.10"))))
                .isInstanceOf(InvalidSettlementTransactionException.class)
                .hasMessageContaining("Duplicate");
    }

    @Test
    void reprocessesTheSameInputInstanceAfterAChunkRetry() {
        SettlementTransactionInput input = input(new BigDecimal("10.00"), "USD", new BigDecimal("0.10"));

        processor.process(input);

        assertThat(processor.process(input)).isNotNull();
    }

    private static SettlementTransactionInput input(BigDecimal amount, String currency, BigDecimal fee) {
        return new SettlementTransactionInput("TX-1", "ACC-SOURCE", "ACC-DEST", amount, currency, fee,
                Instant.parse("2026-08-21T03:15:00Z"), "MERCH-TEST");
    }
}
