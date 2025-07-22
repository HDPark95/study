package project.springratelimiter.ratelimiter.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * RedisRateLimiterService에 대한 단위 테스트.
 * 이 테스트는 속도 제한 로직이 올바르게 작동하는지 확인합니다.
 */
class RedisRateLimiterServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisScript<Boolean> rateLimiterScript;
    
    // 테스트용 SimpleMeterRegistry 사용
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private RedisRateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rateLimiterService = new RedisRateLimiterService(redisTemplate, rateLimiterScript, meterRegistry);
    }

    /**
     * 요청이 속도 제한 내에 있을 때 tryAcquire 메서드가 true를 반환하는지 테스트합니다.
     */
    @Test
    void tryAcquire_WhenUnderLimit_ShouldReturnTrue() {
        // given
        String key = "test-key";
        long limit = 10;
        long period = 60;

        // Lua 스크립트가 true를 반환하도록 설정 (속도 제한 내)
        when(redisTemplate.execute(
                any(RedisScript.class),
                anyList(),
                any(Object[].class))
        ).thenReturn(Boolean.TRUE);

        // when
        boolean result = rateLimiterService.tryAcquire(key, limit, period);

        // then
        assertTrue(result, "속도 제한 내에 있을 때 true를 반환해야 합니다");
    }

    /**
     * 요청이 속도 제한을 초과할 때 tryAcquire 메서드가 false를 반환하는지 테스트합니다.
     */
    @Test
    void tryAcquire_WhenOverLimit_ShouldReturnFalse() {
        // given
        String key = "test-key";
        long limit = 10;
        long period = 60;

        // Lua 스크립트가 false를 반환하도록 설정 (속도 제한 초과)
        when(redisTemplate.execute(
                any(RedisScript.class),
                anyList(),
                any(Object[].class))
        ).thenReturn(Boolean.FALSE);

        // when
        boolean result = rateLimiterService.tryAcquire(key, limit, period);

        // then
        assertFalse(result, "속도 제한을 초과할 때 false를 반환해야 합니다");
    }
}