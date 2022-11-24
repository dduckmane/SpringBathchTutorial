package com.example.springbathchtutorial.job.dbDataReadWrite;

import com.example.springbathchtutorial.core.domain.accounts.Accounts;
import com.example.springbathchtutorial.core.domain.accounts.AccountsRepository;
import com.example.springbathchtutorial.core.domain.orders.Orders;
import com.example.springbathchtutorial.core.domain.orders.OrdersRepository;
import com.example.springbathchtutorial.job.validatorParam.validator.FileParamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j

/**
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.names=TrMigrationJob
 * */
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;

    @Bean
    public Job TrMigrationJob(@Qualifier("TrMigrationStep") Step TrMigrationStep){
        return jobBuilderFactory
                .get("TrMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(TrMigrationStep)
                .build();
    }

    @JobScope//job 밑에서 실행이 되므로
    @Bean
    public Step TrMigrationStep(ItemReader trOrdersReader,ItemProcessor trOrderProcessor,ItemWriter trOrdersWriter){
        return stepBuilderFactory
                .get("TrMigrationStep")
                .<Orders, Accounts> chunk(5)
                .reader(trOrdersReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(trOrderProcessor)
                .writer(trOrdersWriter)
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProcessor(){
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> trOrdersWriter(){
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }


    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader(){
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)//chunkSize와 일치를 시킨다.
                .arguments(Arrays.asList())//여기에 findAll이나 다른 함수에서의 매개변수를 전달한다.
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();

    }


}
