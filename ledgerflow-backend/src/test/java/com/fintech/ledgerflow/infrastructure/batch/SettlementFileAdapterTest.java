package com.fintech.ledgerflow.infrastructure.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fintech.ledgerflow.application.settlement.SettlementInputUnavailableException;
import com.fintech.ledgerflow.infrastructure.config.SettlementProperties;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SettlementFileAdapterTest {
    @Test
    void selectsFirstRegularCsvAndPreparesErrorDirectory(@TempDir Path inputDirectory,
                                                         @TempDir Path errorDirectory) throws Exception {
        Path selectedFile = Files.createFile(inputDirectory.resolve("a-settlement.csv"));
        Files.createFile(inputDirectory.resolve("z-settlement.csv"));
        Files.createDirectory(inputDirectory.resolve("ignored.csv"));
        Path configuredErrorDirectory = errorDirectory.resolve("nested");
        SettlementFileAdapter adapter = new SettlementFileAdapter(
                new SettlementProperties(inputDirectory.toString(), configuredErrorDirectory.toString()));

        var files = adapter.prepare();

        assertThat(files.inputFile()).isEqualTo(selectedFile);
        assertThat(files.errorFile()).isEqualTo(configuredErrorDirectory.resolve("failed_transactions.log"));
        assertThat(Files.isDirectory(configuredErrorDirectory)).isTrue();
    }

    @Test
    void rejectsUnavailableInputDirectory(@TempDir Path directory) {
        Path inputDirectory = directory.resolve("missing");
        SettlementFileAdapter adapter = new SettlementFileAdapter(
                new SettlementProperties(inputDirectory.toString(), directory.toString()));

        assertThatThrownBy(adapter::prepare)
                .isInstanceOf(SettlementInputUnavailableException.class)
                .hasMessage("No settlement CSV file found in input directory: " + inputDirectory);
    }
}
