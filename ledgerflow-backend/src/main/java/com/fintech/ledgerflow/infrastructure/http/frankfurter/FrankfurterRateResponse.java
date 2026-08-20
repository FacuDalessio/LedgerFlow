package com.fintech.ledgerflow.infrastructure.http.frankfurter;

import java.math.BigDecimal;
import java.time.LocalDate;

record FrankfurterRateResponse(LocalDate date, String base, String quote, BigDecimal rate) {
}
