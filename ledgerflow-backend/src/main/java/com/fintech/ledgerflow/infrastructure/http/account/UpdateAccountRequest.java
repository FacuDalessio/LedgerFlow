package com.fintech.ledgerflow.infrastructure.http.account;

import com.fintech.ledgerflow.domain.account.AccountStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record UpdateAccountRequest(
        @NotNull @Pattern(regexp = "[A-Za-z]{3}") String currency,
        @NotNull @DecimalMin(value = "0.0") BigDecimal balance,
        @NotNull AccountStatus status) {
}
