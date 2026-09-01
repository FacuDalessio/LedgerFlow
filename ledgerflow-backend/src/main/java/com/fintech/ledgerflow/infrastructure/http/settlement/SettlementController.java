package com.fintech.ledgerflow.infrastructure.http.settlement;

import com.fintech.ledgerflow.application.settlement.SettlementRun;
import com.fintech.ledgerflow.application.settlement.SettlementUseCase;
import com.fintech.ledgerflow.infrastructure.config.SettlementProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {
    private final SettlementUseCase settlementUseCase;
    private final SettlementProperties properties;

    public SettlementController(SettlementUseCase settlementUseCase, SettlementProperties properties) {
        this.settlementUseCase = settlementUseCase;
        this.properties = properties;
    }

    @PostMapping("/trigger")
    public ResponseEntity<?> trigger() {
        Path inputDirectory = Path.of(properties.inputDirectory());
        Path inputFile;
        try {
            inputFile = selectInputFile(inputDirectory);
        } catch (IOException exception) {
            return ResponseEntity.badRequest()
                    .body("Unable to inspect settlement input directory: " + inputDirectory);
        }
        if (inputFile == null) {
            return ResponseEntity.badRequest()
                    .body("No settlement CSV file found in input directory: " + inputDirectory);
        }

        Path errorFile = Path.of(properties.errorDirectory()).resolve("failed_transactions.log");
        try {
            Files.createDirectories(errorFile.getParent());
        } catch (IOException exception) {
            return ResponseEntity.internalServerError()
                    .body("Unable to prepare settlement error log directory: " + errorFile.getParent());
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(settlementUseCase.trigger(inputFile, errorFile));
    }

    private Path selectInputFile(Path inputDirectory) throws IOException {
        if (!Files.isDirectory(inputDirectory)) {
            return null;
        }
        try (var files = Files.list(inputDirectory)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .findFirst()
                    .orElse(null);
        }
    }
}
