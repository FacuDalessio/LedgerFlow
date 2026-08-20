package com.fintech.ledgerflow.domain.exchangerate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRate(LocalDate date, String base, String quote, BigDecimal rate) {
}
