package com.fintech.ledgerflow.application.exchangerate;

import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;

public interface ExchangeRateProvider {
    ExchangeRate fetch(String baseCurrency, String quoteCurrency);
}
