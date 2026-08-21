package com.fintech.ledgerflow.application.settlement;

import java.nio.file.Path;
import org.springframework.stereotype.Service;

@Service
public class SettlementService implements SettlementUseCase {
    private final SettlementJobLauncher jobLauncher;

    public SettlementService(SettlementJobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Override
    public SettlementRun trigger(Path inputFile, Path errorFile) {
        return jobLauncher.launch(inputFile, errorFile);
    }
}
