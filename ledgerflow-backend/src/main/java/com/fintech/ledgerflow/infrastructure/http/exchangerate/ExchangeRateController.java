package com.fintech.ledgerflow.infrastructure.http.exchangerate;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateUseCase exchangeRateUseCase;

    public ExchangeRateController(ExchangeRateUseCase exchangeRateUseCase) {
        this.exchangeRateUseCase = exchangeRateUseCase;
    }

    @GetMapping("/{base}/{quote}")
    public ExchangeRateResponse get(@PathVariable String base, @PathVariable String quote) {
        return ExchangeRateResponse.from(exchangeRateUseCase.get(base, quote));
    }
}
