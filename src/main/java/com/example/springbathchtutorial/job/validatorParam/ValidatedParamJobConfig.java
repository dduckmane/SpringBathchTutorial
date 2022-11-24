package com.example.springbathchtutorial.job.validatorParam;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j
/**
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.names=validatedParamJob -fileName=test.csv
 * */
public class ValidatedParamJobConfig {

    private final JobBuilderFactory jobBuilderFactory;//job을 생성하기 위해

    private final StepBuilderFactory stepBuilderFactory;//step을 생성하기 위해

    @Bean
    public Job ValidatedParamJob(Step ValidatedParamStep){
        return jobBuilderFactory
                .get("ValidatedParamJob")// JOB의 이름을 부여
                .incrementer(new RunIdIncrementer())//ID 자동 생성
//                .validator(new FileParamValidator())
                .validator(multipleValidator())
                .start(ValidatedParamStep)//job안에 step이 들어간다.
                .build();
    }

    private CompositeJobParametersValidator multipleValidator(){
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator()));

        return validator;
    }

    @JobScope//job 밑에서 실행이 되므로
    @Bean
    public Step ValidatedParamStep(Tasklet ValidatedParamTasklet){
        return stepBuilderFactory
                .get("ValidatedParamStep")
                .tasklet(ValidatedParamTasklet)//itemWriter itemReader가 필요없을 때 taskLet을 사용한다.
                .build();
    }
    @StepScope//Step 밑에서 실행이 되므로
    @Bean
    public Tasklet ValidatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
        return new Tasklet() {//taskLet은 빌더 페턴이 없다.
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("Validated Param Tasklet");
                System.out.println(fileName);

                //여기에 무언가 작업을 하고

                return RepeatStatus.FINISHED;//끝을 내겠다.
            }
        };
    }

}
