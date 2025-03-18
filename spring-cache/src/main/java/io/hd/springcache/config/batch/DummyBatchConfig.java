package io.hd.springcache.config.batch;

import io.hd.springcache.domain.Goods;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

import static org.instancio.Select.field;

@Configuration
@RequiredArgsConstructor
public class DummyBatchConfig {

    public static final int CHUNK_SIZE = 5000;

    public static final int CORE_POOL_SIZE = 5;

    private final EntityManagerFactory emf;

    private final JobRepository jobRepository;

    @Bean
    public Job dummyJob() {
        return new JobBuilder("dummyJob",jobRepository)
                .start(dummyGoodsStep())
                .build();
    }
    private final PlatformTransactionManager transactionManager;

    @JobScope
    @Bean
    public Step dummyGoodsStep() {
        return new StepBuilder("missingOrderItemHistoryStep", jobRepository)
                .<Goods, Goods>chunk(CHUNK_SIZE, transactionManager)
                .reader(dummyDataReader())
                .writer(jpaWriter())
                .build();
    }
    @StepScope
    @Bean
    public ListItemReader<Goods> dummyDataReader() {
        List<Goods> dummyData = Instancio.ofList(Goods.class)
                .size(10000)
                .ignore(field(Goods.class, "id"))
                .create();
        return new ListItemReader<>(dummyData);
    }

    @StepScope
    @Bean
    public <T> JpaItemWriter<T> jpaWriter() {
        JpaItemWriter<T> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        writer.setUsePersist(false);

        return writer;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(CORE_POOL_SIZE * 2);
        taskExecutor.setThreadNamePrefix("batch-async-Thread");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(
                Boolean.TRUE);
        taskExecutor.initialize();
        return taskExecutor;
    }

    private <T> JpaPagingItemReader<T> createReader(String readerName, String query, int chunkSize) {
        return new JpaPagingItemReaderBuilder<T>()
                .name(readerName)
                .entityManagerFactory(emf)
                .queryString(query)
                .saveState(true)
                .pageSize(chunkSize)
                .build();
    }
}
