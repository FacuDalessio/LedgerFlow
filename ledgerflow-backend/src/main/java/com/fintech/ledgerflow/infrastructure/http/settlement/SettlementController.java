package com.fintech.ledgerflow.infrastructure.http.settlement;

import com.fintech.ledgerflow.application.settlement.SettlementRun;
import com.fintech.ledgerflow.application.settlement.SettlementUseCase;
import com.fintech.ledgerflow.infrastructure.config.SettlementProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<SettlementRun> trigger(@RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String fileName = UUID.randomUUID() + "-" + Path.of(file.getOriginalFilename() == null
                ? "settlement.csv" : file.getOriginalFilename()).getFileName();
        Path inputFile = Path.of(properties.inputDirectory()).resolve(fileName);
        Path errorFile = Path.of(properties.errorDirectory()).resolve("failed_transactions.log");
        Files.createDirectories(inputFile.getParent());
        Files.createDirectories(errorFile.getParent());
        file.transferTo(inputFile);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(settlementUseCase.trigger(inputFile, errorFile));
    }
}
