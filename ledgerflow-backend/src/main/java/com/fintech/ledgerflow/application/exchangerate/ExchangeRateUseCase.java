package com.fintech.ledgerflow.application.exchangerate;

import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;

public interface ExchangeRateUseCase {
    ExchangeRate get(String baseCurrency, String quoteCurrency);
}
