package io.hd.springquartz.application.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExampleJobConfiguration {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(ExampleJob.class)
                .withIdentity("exampleJob")
                .storeDurably()
                .usingJobData("key", "value")
                .build();
    }

    @Bean
    public Trigger exampleJobTrigger(JobDetail exampleJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(exampleJobDetail)
                .withIdentity("exampleJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))// every 10 seconds
                .build();
    }

}
