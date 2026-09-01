package com.fintech.ledgerflow.infrastructure.batch;

import com.fintech.ledgerflow.application.settlement.SettlementErrorLogPreparationException;
import com.fintech.ledgerflow.application.settlement.SettlementFilePort;
import com.fintech.ledgerflow.application.settlement.SettlementFiles;
import com.fintech.ledgerflow.application.settlement.SettlementInputUnavailableException;
import com.fintech.ledgerflow.infrastructure.config.SettlementProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class SettlementFileAdapter implements SettlementFilePort {
    private static final String ERROR_LOG_FILE_NAME = "failed_transactions.log";

    private final SettlementProperties properties;

    public SettlementFileAdapter(SettlementProperties properties) {
        this.properties = properties;
    }

    @Override
    public SettlementFiles prepare() {
        Path inputDirectory = Path.of(properties.inputDirectory());
        Path inputFile = selectInputFile(inputDirectory);
        Path errorFile = Path.of(properties.errorDirectory()).resolve(ERROR_LOG_FILE_NAME);
        prepareErrorDirectory(errorFile);
        return new SettlementFiles(inputFile, errorFile);
    }

    private Path selectInputFile(Path inputDirectory) {
        if (!Files.isDirectory(inputDirectory)) {
            throw noCsv(inputDirectory);
        }
        try (var files = Files.list(inputDirectory)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .findFirst()
                    .orElseThrow(() -> noCsv(inputDirectory));
        } catch (IOException exception) {
            throw new SettlementInputUnavailableException(
                    "Unable to inspect settlement input directory: " + inputDirectory);
        }
    }

    private void prepareErrorDirectory(Path errorFile) {
        try {
            Files.createDirectories(errorFile.getParent());
        } catch (IOException exception) {
            throw new SettlementErrorLogPreparationException(
                    "Unable to prepare settlement error log directory: " + errorFile.getParent());
        }
    }

    private SettlementInputUnavailableException noCsv(Path inputDirectory) {
        return new SettlementInputUnavailableException(
                "No settlement CSV file found in input directory: " + inputDirectory);
    }
}
