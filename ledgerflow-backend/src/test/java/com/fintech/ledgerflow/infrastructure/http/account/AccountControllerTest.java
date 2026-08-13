package com.fintech.ledgerflow.infrastructure.http.account;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fintech.ledgerflow.application.account.AccountUseCase;
import com.fintech.ledgerflow.domain.account.Account;
import com.fintech.ledgerflow.domain.account.AccountStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountUseCase accountUseCase;

    @Test
    void createsAnAccount() throws Exception {
        Account account = Account.create("ACC-001", "USD", BigDecimal.TEN, Instant.EPOCH);
        given(accountUseCase.create("ACC-001", "USD", BigDecimal.TEN)).willReturn(account);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC-001\",\"currency\":\"USD\",\"balance\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("ACC-001"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rejectsInvalidCreateRequestWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"\",\"currency\":\"US\",\"balance\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.accountNumber").exists())
                .andExpect(jsonPath("$.fieldErrors.currency").exists())
                .andExpect(jsonPath("$.fieldErrors.balance").exists());
    }

    @Test
    void returnsDeterministicCollectionFromUseCase() throws Exception {
        Account first = Account.create("ACC-001", "USD", BigDecimal.ZERO, Instant.EPOCH);
        Account second = Account.create("ACC-002", "EUR", BigDecimal.ONE, Instant.EPOCH);
        given(accountUseCase.findAll()).willReturn(List.of(first, second));

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-001"))
                .andExpect(jsonPath("$[1].accountNumber").value("ACC-002"));
    }

    @Test
    void updatesAnAccountWithoutAcceptingAnAccountNumber() throws Exception {
        UUID id = UUID.randomUUID();
        Account account = Account.reconstitute(id, "ACC-001", "EUR", BigDecimal.TEN,
                AccountStatus.ACTIVE, Instant.EPOCH, Instant.EPOCH);
        given(accountUseCase.update(id, "EUR", BigDecimal.TEN, AccountStatus.ACTIVE)).willReturn(account);

        mockMvc.perform(put("/api/v1/accounts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currency\":\"EUR\",\"balance\":10,\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-001"));
    }

    @Test
    void deactivatesAnAccountWithoutDeletingIt() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/accounts/{id}", id))
                .andExpect(status().isNoContent());

        org.mockito.BDDMockito.then(accountUseCase).should().deactivate(id);
    }
}
