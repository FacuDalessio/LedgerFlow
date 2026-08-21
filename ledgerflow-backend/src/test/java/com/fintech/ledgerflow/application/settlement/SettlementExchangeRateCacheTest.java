package com.fintech.ledgerflow.application.settlement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateProvider;
import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SettlementExchangeRateCacheTest {
    @Test
    void fetchesEachCurrencyOnlyOnceAndClearsAfterTheJob() {
        ExchangeRateProvider provider = mock(ExchangeRateProvider.class);
        ExchangeRate rate = new ExchangeRate(LocalDate.of(2026, 8, 21), "EUR", "USD", new BigDecimal("1.08"));
        when(provider.fetch("EUR")).thenReturn(rate);
        SettlementExchangeRateCache cache = new SettlementExchangeRateCache();

        assertThat(cache.getOrFetch("EUR", provider)).isEqualTo(rate);
        assertThat(cache.getOrFetch("EUR", provider)).isEqualTo(rate);
        assertThat(cache.size()).isEqualTo(1);
        verify(provider).fetch("EUR");

        cache.clear();

        assertThat(cache.size()).isZero();
    }
}
