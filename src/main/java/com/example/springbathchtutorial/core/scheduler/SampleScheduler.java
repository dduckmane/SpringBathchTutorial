package com.example.springbathchtutorial.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SampleScheduler {
    private final Job helloWorldJob;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "*/15 * * * * *")
    public void helloWorldJobRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        JobParameters jobParameters = new JobParameters(
                Collections.singletonMap("requestTime", new JobParameter(String.valueOf(LocalDateTime.now())))
                //run을 할때는 job과 job파라미터를 넘겨야한다.
                //job 파라미터는 map형식으로 넘겨야한다.
        );

        jobLauncher.run(helloWorldJob,jobParameters);
    }

}
