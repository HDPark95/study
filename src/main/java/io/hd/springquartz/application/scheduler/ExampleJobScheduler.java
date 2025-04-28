package io.hd.springquartz.application.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExampleJobScheduler {
    private final Scheduler scheduler;
    private final JobDetail exampleJob;
    private final Trigger trigger;

    @PostConstruct
    public void schedule() throws SchedulerException {
        if (!scheduler.checkExists(exampleJob.getKey())) {
            scheduler.scheduleJob(exampleJob, trigger);
        }
    }
}
