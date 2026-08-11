package com.fintech.ledgerflow.domain.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AccountTest {

    private static final Instant NOW = Instant.parse("2026-08-11T00:00:00Z");

    @Test
    void createsAnActiveAccountWithNormalizedCurrency() {
        Account account = Account.create("ACC-001", "usd", BigDecimal.TEN, NOW);

        assertThat(account.getAccountNumber()).isEqualTo("ACC-001");
        assertThat(account.getCurrency()).isEqualTo("USD");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getCreatedAt()).isEqualTo(NOW);
    }

    @Test
    void rejectsNegativeBalance() {
        assertThatThrownBy(() -> Account.create("ACC-001", "USD", BigDecimal.valueOf(-1), NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deactivatesWithoutChangingAccountNumber() {
        Account account = Account.create("ACC-001", "USD", BigDecimal.ZERO, NOW);

        account.deactivate(NOW.plusSeconds(1));

        assertThat(account.getStatus()).isEqualTo(AccountStatus.INACTIVE);
        assertThat(account.getAccountNumber()).isEqualTo("ACC-001");
    }
}
