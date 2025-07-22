package project.springratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {"spring.main.allow-circular-references=true"})
class SpringRateLimiterApplicationTests {

    @Test
    void contextLoads() {
    }

}
