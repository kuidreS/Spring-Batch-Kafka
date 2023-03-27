package vitalii.serdiuk.test.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.support.serializer.JsonSerializer;
import vitalii.serdiuk.test.model.Book;

import java.util.Properties;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BookBatchConfig {

    private final KafkaProperties properties;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final BookProcessor bookProcessor;
    private final BookDbWriter bookDbWriter;

    @Autowired
    JobRepository jobRepository;

    @Bean(name = "bookJobLauncher")
    public JobLauncher bookJobLauncher() throws Exception {
        log.info("Starting book job launcher");
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    Job job() {
        log.info("Starting book job");
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(start())
                .build();
    }

    @Bean
    KafkaItemReader<Long, Book> kafkaItemReader() {
        log.info("Starting Kafka item reader");
        Properties props = new Properties();
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        props.putAll(this.properties.buildConsumerProperties());

        return new KafkaItemReaderBuilder<Long, Book>()
                .partitions(0)
                .consumerProperties(props)
                .name("books-reader")
                .saveState(true)
                .topic("books")
                .build();
    }

    @Bean
    Step start() {
        return stepBuilderFactory
                .get("step")
                .<Book, Book>chunk(10)
                .writer(bookDbWriter)
                .processor(bookProcessor)
                .reader(kafkaItemReader())
                .build();
    }
}
