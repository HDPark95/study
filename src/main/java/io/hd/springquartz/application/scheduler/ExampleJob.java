package io.hd.springquartz.application.scheduler;

import io.hd.springquartz.application.example.ExampleService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

public class ExampleJob implements Job {

    @Autowired
    ExampleService exampleService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }

}
