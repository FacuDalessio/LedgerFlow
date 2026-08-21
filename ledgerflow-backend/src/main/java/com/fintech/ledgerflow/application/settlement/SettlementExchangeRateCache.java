package com.fintech.ledgerflow.application.settlement;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateProvider;
import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SettlementExchangeRateCache {
    private final Map<String, ExchangeRate> rates = new ConcurrentHashMap<>();

    public ExchangeRate getOrFetch(String baseCurrency, ExchangeRateProvider provider) {
        return rates.computeIfAbsent(baseCurrency, provider::fetch);
    }

    public void clear() {
        rates.clear();
    }

    public int size() {
        return rates.size();
    }
}
