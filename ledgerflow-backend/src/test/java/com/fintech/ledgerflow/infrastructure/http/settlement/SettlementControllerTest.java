package com.fintech.ledgerflow.infrastructure.http.settlement;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fintech.ledgerflow.application.settlement.SettlementRun;
import com.fintech.ledgerflow.application.settlement.SettlementUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SettlementController.class)
@AutoConfigureMockMvc(addFilters = false)
class SettlementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SettlementUseCase settlementUseCase;

    @Test
    void triggersSettlementWithoutRequestBody() throws Exception {
        SettlementRun run = new SettlementRun(1L, "STARTING");
        willReturn(run).given(settlementUseCase).trigger();

        mockMvc.perform(post("/api/v1/settlements/trigger"))
                .andExpect(status().isAccepted())
                .andExpect(content().json("{\"executionId\":1,\"status\":\"STARTING\"}"));

        then(settlementUseCase).should().trigger();
    }
}
