package com.fintech.ledgerflow.infrastructure.config;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateProvider;
import com.fintech.ledgerflow.infrastructure.http.frankfurter.FrankfurterExchangeRateAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(FrankfurterProperties.class)
public class ExchangeRateConfiguration {

    @Bean
    RestClient frankfurterRestClient(RestClient.Builder builder, FrankfurterProperties properties) {
        return builder.baseUrl(properties.baseUrl().toString()).build();
    }

    @Bean
    ExchangeRateProvider exchangeRateProvider(RestClient frankfurterRestClient) {
        return new FrankfurterExchangeRateAdapter(frankfurterRestClient);
    }
}
