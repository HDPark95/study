package project.springratelimiter.ratelimiter.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import project.springratelimiter.ratelimiter.annotation.RateLimit;
import project.springratelimiter.ratelimiter.annotation.RateLimiterType;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis를 사용한 슬라이딩 윈도우 카운터 알고리즘 기반 속도 제한 서비스 구현.
 * 이 알고리즘은 시간을 작은 버킷으로 나누고 각 버킷의 요청 수를 저장합니다.
 * 현재 윈도우와 이전 윈도우의 가중 합계를 사용하여 속도 제한을 계산합니다.
 * 이 방식은 슬라이딩 윈도우 로그보다 메모리 효율적이며 여전히 윈도우 경계에서 트래픽 급증을 방지합니다.
 */
@Service
@RateLimiterType(RateLimit.Algorithm.SLIDING_WINDOW_COUNTER)
public class SlidingWindowCounterRateLimiterService implements RateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> slidingWindowCounterScript;
    private final MeterRegistry meterRegistry;
    
    // 메트릭 정의
    private final Counter totalRequestsCounter;
    private final Counter allowedRequestsCounter;
    private final Counter rejectedRequestsCounter;
    private final Timer rateLimitTimer;

    /**
     * Redis 템플릿, Lua 스크립트, 메트릭 레지스트리를 사용하여 SlidingWindowCounterRateLimiterService를 생성합니다.
     *
     * @param redisTemplate Redis 작업을 위한 템플릿
     * @param slidingWindowCounterScript 슬라이딩 윈도우 카운터 로직을 구현한 Lua 스크립트
     * @param meterRegistry 메트릭 수집을 위한 레지스트리
     */
    public SlidingWindowCounterRateLimiterService(RedisTemplate<String, Object> redisTemplate, 
                                  RedisScript<Boolean> slidingWindowCounterScript,
                                  MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.slidingWindowCounterScript = slidingWindowCounterScript;
        this.meterRegistry = meterRegistry;
        
        // 메트릭 초기화
        this.totalRequestsCounter = Counter.builder("sliding_window_counter_rate_limiter.requests.total")
                .description("슬라이딩 윈도우 카운터 속도 제한 요청 총 횟수")
                .register(meterRegistry);
                
        this.allowedRequestsCounter = Counter.builder("sliding_window_counter_rate_limiter.requests.allowed")
                .description("슬라이딩 윈도우 카운터 속도 제한 내에서 허용된 요청 횟수")
                .register(meterRegistry);
                
        this.rejectedRequestsCounter = Counter.builder("sliding_window_counter_rate_limiter.requests.rejected")
                .description("슬라이딩 윈도우 카운터 속도 제한을 초과하여 거부된 요청 횟수")
                .register(meterRegistry);
                
        this.rateLimitTimer = Timer.builder("sliding_window_counter_rate_limiter.execution.time")
                .description("슬라이딩 윈도우 카운터 속도 제한 실행 시간")
                .register(meterRegistry);
    }

    /**
     * 주어진 키에 대한 요청이 속도 제한을 초과하는지 확인합니다.
     * 시간을 작은 버킷으로 나누고 각 버킷의 요청 수를 저장하여 슬라이딩 윈도우 카운터 알고리즘을 구현합니다.
     *
     * @param key 속도 제한을 적용할 고유 키 (예: 사용자 ID, IP 주소 등)
     * @param limit 허용된 요청 수
     * @param period 시간 기간(초)
     * @return 요청이 속도 제한 내에 있으면 true, 그렇지 않으면 false
     */
    @Override
    public boolean tryAcquire(String key, long limit, long period) {
        // 총 요청 카운터 증가
        totalRequestsCounter.increment();
        
        // 타이머로 실행 시간 측정 시작
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // 현재 시간을 밀리초 단위로 가져옵니다
            long now = Instant.now().toEpochMilli();
            
            // 키 이름 생성 (예: sliding_window_counter:user_123)
            String redisKey = "sliding_window_counter:" + key;
            
            // Lua 스크립트 실행에 필요한 키와 인자 준비
            List<String> keys = Collections.singletonList(redisKey);
            Object[] args = { now, limit, period * 1000 }; // period를 밀리초로 변환
            
            // Lua 스크립트 실행 및 결과 저장
            boolean allowed = Boolean.TRUE.equals(redisTemplate.execute(slidingWindowCounterScript, keys, args));
            
            // 결과에 따라 적절한 카운터 증가
            if (allowed) {
                allowedRequestsCounter.increment();
            } else {
                rejectedRequestsCounter.increment();
            }
            
            return allowed;
        } finally {
            // 타이머로 실행 시간 측정 종료 및 기록
            sample.stop(rateLimitTimer);
        }
    }
}