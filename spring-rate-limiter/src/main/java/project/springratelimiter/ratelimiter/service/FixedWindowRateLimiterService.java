package project.springratelimiter.ratelimiter.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import project.springratelimiter.ratelimiter.annotation.RateLimit;
import project.springratelimiter.ratelimiter.annotation.RateLimiterType;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Redis를 사용한 고정 윈도우 카운터 알고리즘 기반 속도 제한 서비스 구현.
 * 이 알고리즘은 시간을 고정된 윈도우로 나누고 각 윈도우 내의 요청 수를 제한합니다.
 * 구현이 간단하지만 윈도우 경계에서 트래픽 급증이 발생할 수 있습니다.
 */
@Service
@RateLimiterType(RateLimit.Algorithm.FIXED_WINDOW)
public class FixedWindowRateLimiterService implements RateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MeterRegistry meterRegistry;
    
    // 메트릭 정의
    private final Counter totalRequestsCounter;
    private final Counter allowedRequestsCounter;
    private final Counter rejectedRequestsCounter;
    private final Timer rateLimitTimer;

    /**
     * Redis 템플릿과 메트릭 레지스트리를 사용하여 FixedWindowRateLimiterService를 생성합니다.
     *
     * @param redisTemplate Redis 작업을 위한 템플릿
     * @param meterRegistry 메트릭 수집을 위한 레지스트리
     */
    public FixedWindowRateLimiterService(RedisTemplate<String, Object> redisTemplate, 
                                        MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.meterRegistry = meterRegistry;
        
        // 메트릭 초기화
        this.totalRequestsCounter = Counter.builder("fixed_window_rate_limiter.requests.total")
                .description("고정 윈도우 속도 제한 요청 총 횟수")
                .register(meterRegistry);
                
        this.allowedRequestsCounter = Counter.builder("fixed_window_rate_limiter.requests.allowed")
                .description("고정 윈도우 속도 제한 내에서 허용된 요청 횟수")
                .register(meterRegistry);
                
        this.rejectedRequestsCounter = Counter.builder("fixed_window_rate_limiter.requests.rejected")
                .description("고정 윈도우 속도 제한을 초과하여 거부된 요청 횟수")
                .register(meterRegistry);
                
        this.rateLimitTimer = Timer.builder("fixed_window_rate_limiter.execution.time")
                .description("고정 윈도우 속도 제한 실행 시간")
                .register(meterRegistry);
    }

    /**
     * 주어진 키에 대한 요청이 속도 제한을 초과하는지 확인합니다.
     * Redis의 키-값 저장소를 사용하여 고정 윈도우 내의 요청 수를 추적합니다.
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
            // 현재 시간을 초 단위로 가져옵니다
            long now = Instant.now().getEpochSecond();
            
            // 현재 윈도우의 시작 시간 계산 (현재 시간을 기간으로 나눈 몫 * 기간)
            long windowStart = (now / period) * period;
            
            // 키 이름 생성 (예: fixed_window:user_123:1627776000)
            String redisKey = String.format("fixed_window:%s:%d", key, windowStart);
            
            // 현재 윈도우의 요청 수 증가 및 가져오기
            Long count = redisTemplate.opsForValue().increment(redisKey, 1);
            
            // 키가 없었다면 만료 시간 설정 (다음 윈도우 시작 시간까지)
            if (count != null && count == 1) {
                // 현재 윈도우의 종료 시간 계산 (다음 윈도우의 시작 시간)
                long windowEnd = windowStart + period;
                // 만료 시간 설정 (현재 시간부터 윈도우 종료 시간까지)
                long ttl = windowEnd - now;
                redisTemplate.expire(redisKey, ttl, TimeUnit.SECONDS);
            }
            
            // 요청 수가 제한보다 작거나 같으면 허용
            boolean allowed = count != null && count <= limit;
            
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