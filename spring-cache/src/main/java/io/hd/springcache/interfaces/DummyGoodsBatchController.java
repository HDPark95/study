package io.hd.springcache.interfaces;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DummyGoodsBatchController {
    private final JobLauncher jobLauncher;
    private final Job dummyJob;

    @PostMapping("/dummy-data")
    public ResponseEntity<String> launchDummyJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(dummyJob, jobParameters);
            return ResponseEntity.ok("Dummy job has been launched successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to launch dummy job: " + e.getMessage());
        }
    }
}
