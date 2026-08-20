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
    public ExchangeRate fetch(String baseCurrency, String quoteCurrency) {
        try {
            FrankfurterRateResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/v2/rate/{base}/{quote}")
                            .build(baseCurrency, quoteCurrency))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, statusResponse) -> {
                        throw new ExchangeRateUnavailableException(
                                "Frankfurter returned status " + statusResponse.getStatusCode().value(), null);
                    })
                    .body(FrankfurterRateResponse.class);
            if (response == null) {
                throw new ExchangeRateUnavailableException("Frankfurter returned an empty response", null);
            }
            return new ExchangeRate(response.date(), response.base(), response.quote(), response.rate());
        } catch (ExchangeRateUnavailableException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ExchangeRateUnavailableException("Unable to retrieve exchange rate from Frankfurter", exception);
        }
    }
}
