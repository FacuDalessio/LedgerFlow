package com.fintech.ledgerflow.application.settlement;

public class SettlementInputUnavailableException extends RuntimeException {
    public SettlementInputUnavailableException(String message) {
        super(message);
    }
}
