package com.fintech.ledgerflow.application.settlement;

import java.nio.file.Path;

public interface SettlementUseCase {
    SettlementRun trigger(Path inputFile, Path errorFile);
}
