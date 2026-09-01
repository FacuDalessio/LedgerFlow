package com.fintech.ledgerflow.application.settlement;

import java.nio.file.Path;

public record SettlementFiles(Path inputFile, Path errorFile) {
}
