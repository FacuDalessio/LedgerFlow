package com.fintech.ledgerflow.infrastructure.http.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank String accountNumber,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency,
        @NotNull @DecimalMin(value = "0.0") BigDecimal balance) {
}
