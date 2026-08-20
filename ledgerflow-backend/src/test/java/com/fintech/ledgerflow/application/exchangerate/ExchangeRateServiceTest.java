package com.fintech.ledgerflow.application.exchangerate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateProvider provider;

    @Test
    void normalizesCurrencyCodesBeforeCallingProvider() {
        ExchangeRateService service = new ExchangeRateService(provider);

        service.get("usd", "eur");

        verify(provider).fetch("USD", "EUR");
    }

    @Test
    void rejectsCurrencyCodesWithAnInvalidShape() {
        ExchangeRateService service = new ExchangeRateService(provider);

        assertThatThrownBy(() -> service.get("US", "EUR"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Currency codes must contain exactly three letters");
    }
}
