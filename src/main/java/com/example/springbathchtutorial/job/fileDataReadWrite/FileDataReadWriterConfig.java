package com.example.springbathchtutorial.job.fileDataReadWrite;

import com.example.springbathchtutorial.job.fileDataReadWrite.dto.Player;
import com.example.springbathchtutorial.job.fileDataReadWrite.dto.PlayerYears;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
/**
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.names=fileReadWriteJob
 * */
public class FileDataReadWriterConfig {

    private final JobBuilderFactory jobBuilderFactory;//job을 생성하기 위해

    private final StepBuilderFactory stepBuilderFactory;//step을 생성하기 위해

    @Bean
    public Job fileReadWriteJob(Step fileReadWriteStep){
        return jobBuilderFactory
                .get("fileReadWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileReadWriteStep)
                .build();
    }



    @JobScope//job 밑에서 실행이 되므로
    @Bean
    public Step fileReadWriteStep(ItemReader playerItemReader,ItemProcessor playerYearsItemProcessor,ItemWriter playerItemWriter){
        return stepBuilderFactory
                .get("fileReadWriteStep")
                .<Player, PlayerYears>chunk(5)
                .reader(playerItemReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(playerYearsItemProcessor)
                .writer(playerItemWriter)
                .build();
    }
    @StepScope
    @Bean
    public ItemProcessor<Player,PlayerYears> playerYearsItemProcessor(){
        return new ItemProcessor<Player, PlayerYears>() {
            @Override
            public PlayerYears process(Player item) throws Exception {
                return new PlayerYears(item);
            }
        };
    }

    //repo에서 읽어올때는 repositoryReader를 파일에서 읽어올시에는 이걸 사용
    @StepScope
    @Bean
    public FlatFileItemReader<Player> playerItemReader(){
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader")
                .resource(new FileSystemResource("Players.csv"))//어디 파일을 읽을 건지
                .lineTokenizer(new DelimitedLineTokenizer())// 쉼표로 라인을 구분하겠다.
                .fieldSetMapper(new PlayerFieldSetMapper())//파일에서 객체로 mapper가 필요하다.
                .linesToSkip(1)//첫번째 줄은 스킵을 하겠다.
                .build();
    }
    @StepScope
    @Bean
    public FlatFileItemWriter<PlayerYears> playerItemWriter(){

        //어떤 필드를 뽑아올건지 결정
        BeanWrapperFieldExtractor<PlayerYears> fieldExtractor =new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID","lastName","position","yearsExperience"});
        fieldExtractor.afterPropertiesSet();
        //

        DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();//어떤 기준으로 만드냐
        lineAggregator.setDelimiter(",");//쉼표로 구분하겠다.
        lineAggregator.setFieldExtractor(fieldExtractor);//데이터를 받아서 extractor로 뽑아내고 그 데이터를 쉼표로 구분하겠다.

        FileSystemResource outputResource =new FileSystemResource("player_output.txt");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();

    }


}
