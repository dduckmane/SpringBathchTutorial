package com.example.springbathchtutorial.job.validatorParam.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class FileParamValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        //여기서는 파일이름이 유효한지 검증하는 로직을 짠다.
        String fileName = parameters.getString("fileName");

        if(!StringUtils.endsWithIgnoreCase(fileName,"csv"))throw new JobParametersInvalidException("this is not csv file");
    }
}
