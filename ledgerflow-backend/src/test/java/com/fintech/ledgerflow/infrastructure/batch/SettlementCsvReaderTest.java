package com.fintech.ledgerflow.infrastructure.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.fintech.ledgerflow.domain.settlement.SettlementTransactionInput;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SettlementCsvReaderTest {
    @Test
    void readsTheDocumentedCsvColumns() throws Exception {
        Path file = Files.createTempFile("settlement", ".csv");
        Files.writeString(file, "external_tx_id,source_account,destination_account,amount,currency,fee_amount,"
                + "timestamp,merchant_code\nTX-1,ACC-SOURCE,ACC-DEST,150.00,USD,1.50,"
                + "2026-08-21T03:15:00Z,MERCH-TEST\n");

        var reader = SettlementCsvReader.create(file.toString());
        reader.open(new org.springframework.batch.infrastructure.item.ExecutionContext());
        SettlementTransactionInput item = reader.read();

        assertThat(item.externalTransactionId()).isEqualTo("TX-1");
        assertThat(item.amount()).isEqualByComparingTo("150.00");
        assertThat(item.merchantCode()).isEqualTo("MERCH-TEST");
        reader.close();
        Files.deleteIfExists(file);
    }
}
