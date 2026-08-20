package com.fintech.ledgerflow.infrastructure.http.frankfurter;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateProvider;
import com.fintech.ledgerflow.application.exchangerate.ExchangeRateUnavailableException;
import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class FrankfurterExchangeRateAdapter implements ExchangeRateProvider {

    private final RestClient restClient;

    public FrankfurterExchangeRateAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ExchangeRate fetch(String baseCurrency) {
        try {
            ExchangeRate response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/v2/rate/{base}/USD")
                            .build(baseCurrency))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, statusResponse) -> {
                        throw new ExchangeRateUnavailableException(
                                "Frankfurter returned status " + statusResponse.getStatusCode().value(), null);
                    })
                    .body(ExchangeRate.class);
            if (response == null) {
                throw new ExchangeRateUnavailableException("Frankfurter returned an empty response", null);
            }
            return response;
        } catch (ExchangeRateUnavailableException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ExchangeRateUnavailableException("Unable to retrieve exchange rate from Frankfurter", exception);
        }
    }
}
