package com.fintech.ledgerflow.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "settlement")
public record SettlementProperties(String inputDirectory, String errorDirectory) {
}
