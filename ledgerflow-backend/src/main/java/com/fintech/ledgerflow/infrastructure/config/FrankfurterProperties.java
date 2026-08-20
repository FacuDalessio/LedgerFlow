package com.fintech.ledgerflow.infrastructure.config;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frankfurter")
public record FrankfurterProperties(URI baseUrl) {
}
