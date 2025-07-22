package project.springratelimiter;

import org.springframework.boot.SpringApplication;

public class TestSpringRateLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringRateLimiterApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
