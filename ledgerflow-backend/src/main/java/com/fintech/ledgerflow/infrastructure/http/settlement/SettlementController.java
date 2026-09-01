package com.fintech.ledgerflow.infrastructure.http.settlement;

import com.fintech.ledgerflow.application.settlement.SettlementRun;
import com.fintech.ledgerflow.application.settlement.SettlementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {
    private final SettlementUseCase settlementUseCase;

    public SettlementController(SettlementUseCase settlementUseCase) {
        this.settlementUseCase = settlementUseCase;
    }

    @PostMapping("/trigger")
    public ResponseEntity<?> trigger() {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(settlementUseCase.trigger());
    }
}
