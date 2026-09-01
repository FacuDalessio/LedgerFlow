package com.fintech.ledgerflow.application.settlement;

public class InvalidSettlementTransactionException extends RuntimeException {
    public InvalidSettlementTransactionException(String message) {
        super(message);
    }
}
