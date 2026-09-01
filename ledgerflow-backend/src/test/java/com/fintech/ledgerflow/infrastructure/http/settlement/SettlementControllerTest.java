package com.fintech.ledgerflow.infrastructure.http.settlement;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fintech.ledgerflow.application.settlement.SettlementRun;
import com.fintech.ledgerflow.application.settlement.SettlementUseCase;
import com.fintech.ledgerflow.infrastructure.config.SettlementProperties;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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

    @MockitoBean
    private SettlementProperties properties;

    @Test
    void returnsBadRequestWhenInputDirectoryHasNoFile(@TempDir Path inputDirectory) throws Exception {
        willReturn(inputDirectory.toString()).given(properties).inputDirectory();

        mockMvc.perform(post("/api/v1/settlements/trigger"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No settlement CSV file found in input directory: "
                        + inputDirectory));

        then(settlementUseCase).shouldHaveNoInteractions();
    }

    @Test
    void returnsBadRequestWhenInputDirectoryHasNoCsv(@TempDir Path inputDirectory) throws Exception {
        Files.createFile(inputDirectory.resolve("settlement.txt"));
        willReturn(inputDirectory.toString()).given(properties).inputDirectory();

        mockMvc.perform(post("/api/v1/settlements/trigger"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No settlement CSV file found in input directory: "
                        + inputDirectory));

        then(settlementUseCase).shouldHaveNoInteractions();
    }

    @Test
    void launchesJobWithFirstRegularCsvInFilenameOrder(@TempDir Path inputDirectory,
                                                        @TempDir Path errorDirectory) throws Exception {
        Path selectedFile = Files.createFile(inputDirectory.resolve("a-settlement.csv"));
        Files.createFile(inputDirectory.resolve("z-settlement.csv"));
        Files.createDirectory(inputDirectory.resolve("ignored.csv"));
        Path errorFile = errorDirectory.resolve("failed_transactions.log");
        SettlementRun run = new SettlementRun(1L, "STARTING");
        willReturn(inputDirectory.toString()).given(properties).inputDirectory();
        willReturn(errorDirectory.toString()).given(properties).errorDirectory();
        willReturn(run).given(settlementUseCase).trigger(selectedFile, errorFile);

        mockMvc.perform(post("/api/v1/settlements/trigger"))
                .andExpect(status().isAccepted())
                .andExpect(content().json("{\"executionId\":1,\"status\":\"STARTING\"}"));

        then(settlementUseCase).should().trigger(selectedFile, errorFile);
    }
}
