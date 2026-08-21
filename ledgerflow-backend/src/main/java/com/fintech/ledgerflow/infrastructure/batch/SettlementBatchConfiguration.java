package com.fintech.ledgerflow.infrastructure.batch;

import com.fintech.ledgerflow.application.exchangerate.ExchangeRateProvider;
import com.fintech.ledgerflow.application.settlement.InvalidSettlementTransactionException;
import com.fintech.ledgerflow.application.settlement.SettlementExchangeRateCache;
import com.fintech.ledgerflow.application.settlement.SettlementItemProcessor;
import com.fintech.ledgerflow.application.settlement.SettlementJobLauncher;
import com.fintech.ledgerflow.application.settlement.SettlementRun;
import com.fintech.ledgerflow.domain.account.AccountRepository;
import com.fintech.ledgerflow.domain.settlement.SettlementTransaction;
import com.fintech.ledgerflow.domain.settlement.SettlementTransactionInput;
import com.fintech.ledgerflow.domain.settlement.SettlementTransactionRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;

@Configuration
@EnableBatchProcessing
public class SettlementBatchConfiguration {
    private static final int CHUNK_SIZE = 1_000;

    @Bean
    @JobScope
    SettlementExchangeRateCache settlementExchangeRateCache() {
        return new SettlementExchangeRateCache();
    }

    @Bean
    @StepScope
    ItemReader<SettlementTransactionInput> settlementReader(
            @Value("#{jobParameters['input.file']}") String inputFile) {
        return SettlementCsvReader.create(inputFile);
    }

    @Bean
    @StepScope
    SettlementItemProcessor settlementProcessor(AccountRepository accountRepository,
                                                SettlementTransactionRepository transactionRepository,
                                                ExchangeRateProvider exchangeRateProvider,
                                                SettlementExchangeRateCache exchangeRateCache) {
        return new SettlementItemProcessor(accountRepository, transactionRepository, exchangeRateProvider,
                exchangeRateCache);
    }

    @Bean
    ItemWriter<SettlementTransaction> settlementWriter(SettlementTransactionRepository repository) {
        return chunk -> chunk.getItems().forEach(repository::save);
    }

    @Bean
    @JobScope
    JobExecutionListener settlementCacheCleanupListener(SettlementExchangeRateCache cache) {
        return new JobExecutionListener() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                cache.clear();
            }
        };
    }

    @Bean
    @StepScope
    SkipListener<SettlementTransactionInput, SettlementTransaction> settlementSkipLogger(
            @Value("#{jobParameters['error.file']}") String errorFile) {
        return new SettlementSkipLogger(Path.of(errorFile));
    }

    @Bean
    Step settlementStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                        ItemReader<SettlementTransactionInput> settlementReader,
                        SettlementItemProcessor settlementProcessor,
                        ItemWriter<SettlementTransaction> settlementWriter,
                        SkipListener<SettlementTransactionInput, SettlementTransaction> settlementSkipLogger) {
        return new StepBuilder("settlementStep", jobRepository)
                .<SettlementTransactionInput, SettlementTransaction>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(settlementReader)
                .processor((ItemProcessor<SettlementTransactionInput, SettlementTransaction>)
                        settlementProcessor::process)
                .writer(settlementWriter)
                .faultTolerant()
                .retry(DataAccessException.class)
                .retryLimit(3)
                .skip(FlatFileParseException.class)
                .skip(InvalidSettlementTransactionException.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(settlementSkipLogger)
                .build();
    }

    @Bean
    Job settlementJob(JobRepository jobRepository, Step settlementStep,
                      JobExecutionListener settlementCacheCleanupListener) {
        return new JobBuilder("settlementJob", jobRepository)
                .listener(settlementCacheCleanupListener)
                .start(settlementStep)
                .build();
    }

    @Bean
    SettlementJobLauncher settlementJobLauncher(JobLauncher jobLauncher, Job settlementJob) {
        return (inputFile, errorFile) -> {
            JobParameters parameters = new JobParametersBuilder()
                    .addString("input.file", inputFile.toAbsolutePath().toString())
                    .addString("error.file", errorFile.toAbsolutePath().toString())
                    .addLong("runId", Instant.now().toEpochMilli())
                    .toJobParameters();
            try {
                JobExecution execution = jobLauncher.run(settlementJob, parameters);
                return new SettlementRun(execution.getId(), execution.getStatus().name());
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to launch settlement job", exception);
            }
        };
    }

    private static final class SettlementSkipLogger
            implements SkipListener<SettlementTransactionInput, SettlementTransaction> {
        private final Path errorFile;

        private SettlementSkipLogger(Path errorFile) {
            this.errorFile = errorFile;
        }

        @Override
        public void onSkipInRead(Throwable throwable) {
            append(throwable instanceof FlatFileParseException parseException
                    ? parseException.getInput() : throwable.getMessage());
        }

        @Override
        public void onSkipInProcess(SettlementTransactionInput item, Throwable throwable) {
            append(item + " - " + throwable.getMessage());
        }

        @Override
        public void onSkipInWrite(SettlementTransaction item, Throwable throwable) {
            append(item.externalTransactionId() + " - " + throwable.getMessage());
        }

        private void append(String line) {
            try {
                Files.writeString(errorFile, line + System.lineSeparator(), StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to write failed transaction log", exception);
            }
        }
    }
}
