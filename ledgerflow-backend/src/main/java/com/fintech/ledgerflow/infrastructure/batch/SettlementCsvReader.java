package com.fintech.ledgerflow.infrastructure.batch;

import com.fintech.ledgerflow.domain.settlement.SettlementTransactionInput;
import java.time.Instant;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.FileSystemResource;

final class SettlementCsvReader {
    private SettlementCsvReader() {
    }

    static FlatFileItemReader<SettlementTransactionInput> create(String filePath) {
        return new FlatFileItemReaderBuilder<SettlementTransactionInput>()
                .name("settlementCsvReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .maxItemCount(100_000)
                .delimited()
                .names("externalTransactionId", "sourceAccount", "destinationAccount", "amount", "currency",
                        "feeAmount", "transactionTimestamp", "merchantCode")
                .fieldSetMapper(fieldSet -> new SettlementTransactionInput(
                        fieldSet.readString("externalTransactionId"), fieldSet.readString("sourceAccount"),
                        fieldSet.readString("destinationAccount"), fieldSet.readBigDecimal("amount"),
                        fieldSet.readString("currency"), fieldSet.readBigDecimal("feeAmount"),
                        Instant.parse(fieldSet.readString("transactionTimestamp")),
                        fieldSet.readString("merchantCode")))
                .build();
    }
}
