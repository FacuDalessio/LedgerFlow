package com.fintech.ledgerflow.infrastructure.http.exchangerate;

import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateResponse(LocalDate date, String base, String quote, BigDecimal rate) {
    public static ExchangeRateResponse from(ExchangeRate exchangeRate) {
        return new ExchangeRateResponse(exchangeRate.date(), exchangeRate.base(),
                exchangeRate.quote(), exchangeRate.rate());
    }
}
