package com.fintech.ledgerflow.application.settlement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {
    @Mock
    private SettlementFilePort settlementFilePort;

    @Mock
    private SettlementJobLauncher jobLauncher;

    @Test
    void preparesSettlementFilesBeforeLaunchingJob() {
        Path inputFile = Path.of("input.csv");
        Path errorFile = Path.of("failed_transactions.log");
        SettlementRun run = new SettlementRun(1L, "STARTING");
        when(settlementFilePort.prepare()).thenReturn(new SettlementFiles(inputFile, errorFile));
        when(jobLauncher.launch(inputFile, errorFile)).thenReturn(run);

        SettlementRun result = new SettlementService(settlementFilePort, jobLauncher).trigger();

        assertThat(result).isEqualTo(run);
        verify(settlementFilePort).prepare();
        verify(jobLauncher).launch(inputFile, errorFile);
    }
}
