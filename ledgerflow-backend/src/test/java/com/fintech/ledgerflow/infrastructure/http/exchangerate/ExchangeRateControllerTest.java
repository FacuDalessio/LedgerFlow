package com.fintech.ledgerflow.infrastructure.http.exchangerate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateUseCase;
import com.fintech.ledgerflow.domain.exchangerate.ExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExchangeRateController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeRateUseCase exchangeRateUseCase;

    @Test
    void returnsAnExchangeRateFromTheUseCase() throws Exception {
        given(exchangeRateUseCase.get("USD", "EUR"))
                .willReturn(new ExchangeRate(LocalDate.of(2026, 8, 11), "USD", "EUR", new BigDecimal("0.8653")));

        mockMvc.perform(get("/api/v1/exchange-rates/USD/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-08-11"))
                .andExpect(jsonPath("$.base").value("USD"))
                .andExpect(jsonPath("$.quote").value("EUR"))
                .andExpect(jsonPath("$.rate").value(0.8653));
    }
}
