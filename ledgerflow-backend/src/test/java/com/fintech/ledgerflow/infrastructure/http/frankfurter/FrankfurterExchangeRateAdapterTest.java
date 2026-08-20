package com.fintech.ledgerflow.infrastructure.http.frankfurter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class FrankfurterExchangeRateAdapterTest {

    @Test
    void callsFrankfurterAndMapsItsResponse() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.frankfurter.dev");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        FrankfurterExchangeRateAdapter adapter = new FrankfurterExchangeRateAdapter(builder.build());
        server.expect(requestTo("https://api.frankfurter.dev/v2/rate/USD/EUR"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"date\":\"2026-08-11\",\"base\":\"USD\",\"quote\":\"EUR\",\"rate\":0.8653}",
                        MediaType.APPLICATION_JSON));

        ExchangeRate exchangeRate = adapter.fetch("USD", "EUR");

        assertThat(exchangeRate.base()).isEqualTo("USD");
        assertThat(exchangeRate.quote()).isEqualTo("EUR");
        assertThat(exchangeRate.rate()).isEqualByComparingTo(new BigDecimal("0.8653"));
        server.verify();
    }
}
