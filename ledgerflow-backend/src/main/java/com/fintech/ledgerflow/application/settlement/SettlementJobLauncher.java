package com.fintech.ledgerflow.application.settlement;

import java.nio.file.Path;

public interface SettlementJobLauncher {
    SettlementRun launch(Path inputFile, Path errorFile);
}
