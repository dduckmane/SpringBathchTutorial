package com.example.springbathchtutorial.job.helloworld;

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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HelloWorldJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;//job을 생성하기 위해

    private final StepBuilderFactory stepBuilderFactory;//step을 생성하기 위해

    private final OrdersRepository ordersRepository;

    @Bean
    public Job helloWorldJob(){
        return jobBuilderFactory
                .get("helloWorldJob")// JOB의 이름을 부여
                .incrementer(new RunIdIncrementer())//ID 자동 생성
                .start(helloWorldStep())//job안에 step이 들어간다.
                .build();
    }

    @JobScope//job 밑에서 실행이 되므로
    @Bean
    public Step helloWorldStep(){
        return stepBuilderFactory
                .get("helloWorldStep")
                .tasklet(helloWorldTasklet())//itemWriter itemReader가 필요없을 때 taskLet을 사용한다.
                .build();
    }
    @StepScope//Step 밑에서 실행이 되므로
    @Bean
    public Tasklet helloWorldTasklet() {
        return new Tasklet() {//taskLet은 빌더 페턴이 없다.
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("Hello World Spring Batch");

                //여기에 무언가 작업을 하고

                return RepeatStatus.FINISHED;//끝을 내겠다.
            }
        };
    }

}
