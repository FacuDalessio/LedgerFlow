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
    void callsFrankfurterForUsdAndMapsItsResponseToTheDomainRecord() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.frankfurter.dev");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        FrankfurterExchangeRateAdapter adapter = new FrankfurterExchangeRateAdapter(builder.build());
        server.expect(requestTo("https://api.frankfurter.dev/v2/rate/EUR/USD"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"date\":\"2026-08-11\",\"base\":\"EUR\",\"quote\":\"USD\",\"rate\":1.1556}",
                        MediaType.APPLICATION_JSON));

        ExchangeRate exchangeRate = adapter.fetch("EUR");

        assertThat(exchangeRate.base()).isEqualTo("EUR");
        assertThat(exchangeRate.quote()).isEqualTo("USD");
        assertThat(exchangeRate.rate()).isEqualByComparingTo(new BigDecimal("1.1556"));
        server.verify();
    }
}
