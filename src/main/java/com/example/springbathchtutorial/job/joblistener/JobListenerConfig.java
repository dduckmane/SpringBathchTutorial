package com.example.springbathchtutorial.job.joblistener;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j

/**
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.names=JobListenerJob
 * */

public class JobListenerConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job JobListenerJob(Step JobListenerStep){
        return jobBuilderFactory
                .get("JobListenerJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(JobListenerStep)
                .build();
    }

    @JobScope//job 밑에서 실행이 되므로
    @Bean
    public Step JobListenerStep(Tasklet JobListenerTasklet){
        return stepBuilderFactory
                .get("JobListenerStep")
                .tasklet(JobListenerTasklet)
                .build();
    }
    @StepScope//Step 밑에서 실행이 되므로
    @Bean
    public Tasklet JobListenerTasklet() {
        return new Tasklet() {//taskLet은 빌더 페턴이 없다.
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("Job Listener Tasklet");

                //여기에 무언가 작업을 하고
//                throw new Exception("test Exception");
                return RepeatStatus.FINISHED;//끝을 내겠다.
            }
        };
    }

}
