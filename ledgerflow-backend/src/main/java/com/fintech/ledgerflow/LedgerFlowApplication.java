package com.fintech.ledgerflow;

import com.fintech.ledgerflow.infrastructure.config.SettlementProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SettlementProperties.class)
public class LedgerFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(LedgerFlowApplication.class, args);
    }
}
