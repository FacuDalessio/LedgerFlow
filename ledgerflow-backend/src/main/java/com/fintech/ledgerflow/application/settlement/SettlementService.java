package com.fintech.ledgerflow.application.settlement;

import org.springframework.stereotype.Service;

@Service
public class SettlementService implements SettlementUseCase {
    private final SettlementFilePort settlementFilePort;
    private final SettlementJobLauncher jobLauncher;

    public SettlementService(SettlementFilePort settlementFilePort, SettlementJobLauncher jobLauncher) {
        this.settlementFilePort = settlementFilePort;
        this.jobLauncher = jobLauncher;
    }

    @Override
    public SettlementRun trigger() {
        SettlementFiles files = settlementFilePort.prepare();
        return jobLauncher.launch(files.inputFile(), files.errorFile());
    }
}
