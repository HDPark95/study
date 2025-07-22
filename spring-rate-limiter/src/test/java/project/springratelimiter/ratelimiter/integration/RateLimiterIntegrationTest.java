package project.springratelimiter.ratelimiter.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import project.springratelimiter.ratelimiter.service.RateLimiterService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 속도 제한 기능에 대한 통합 테스트.
 * 이 테스트는 실제 Redis 인스턴스를 사용하여 속도 제한 기능을 검증합니다.
 */
@SpringBootTest
@Testcontainers
@Import(RateLimiterIntegrationTest.RedisTestConfiguration.class)
class RateLimiterIntegrationTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    /**
     * 속도 제한 내에서 요청이 허용되는지 테스트합니다.
     */
    @Test
    void shouldAllowRequestsWithinLimit() {
        // given
        String key = "test-integration-key";
        long limit = 5;
        long period = 10;

        // when & then
        // 제한 내의 요청은 모두 허용되어야 함
        for (int i = 0; i < limit; i++) {
            assertTrue(rateLimiterService.tryAcquire(key, limit, period),
                    "제한 내의 요청 " + (i + 1) + "은(는) 허용되어야 합니다");
        }

        // 제한을 초과한 요청은 거부되어야 함
        assertFalse(rateLimiterService.tryAcquire(key, limit, period),
                "제한을 초과한 요청은 거부되어야 합니다");
    }

    /**
     * 테스트를 위한 Redis 컨테이너 설정.
     */
    @TestConfiguration
    static class RedisTestConfiguration {

        @Container
        @ServiceConnection
        static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379);
    }
}