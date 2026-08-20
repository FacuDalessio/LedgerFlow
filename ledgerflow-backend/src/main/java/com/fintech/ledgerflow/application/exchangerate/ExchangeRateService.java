package com.fintech.ledgerflow.application.exchangerate;

import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService implements ExchangeRateUseCase {

    private final ExchangeRateProvider provider;

    public ExchangeRateService(ExchangeRateProvider provider) {
        this.provider = provider;
    }

    @Override
    public ExchangeRate get(String baseCurrency, String quoteCurrency) {
        return provider.fetch(normalize(baseCurrency), normalize(quoteCurrency));
    }

    private static String normalize(String currency) {
        if (currency == null || !currency.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Currency codes must contain exactly three letters");
        }
        return currency.toUpperCase(Locale.ROOT);
    }
}
