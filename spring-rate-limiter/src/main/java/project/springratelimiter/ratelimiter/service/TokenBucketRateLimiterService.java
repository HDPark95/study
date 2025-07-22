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
 * Redis를 사용한 토큰 버킷 알고리즘 기반 속도 제한 서비스 구현.
 * 이 알고리즘은 일정 속도로 토큰을 생성하고 각 요청마다 토큰을 소비합니다.
 * 버킷 크기까지 토큰을 저장할 수 있어 일시적인 트래픽 급증을 허용합니다.
 */
@Service
@RateLimiterType(RateLimit.Algorithm.TOKEN_BUCKET)
public class TokenBucketRateLimiterService implements RateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Boolean> tokenBucketScript;
    private final MeterRegistry meterRegistry;
    
    // 메트릭 정의
    private final Counter totalRequestsCounter;
    private final Counter allowedRequestsCounter;
    private final Counter rejectedRequestsCounter;
    private final Timer rateLimitTimer;

    /**
     * Redis 템플릿, Lua 스크립트, 메트릭 레지스트리를 사용하여 TokenBucketRateLimiterService를 생성합니다.
     *
     * @param redisTemplate Redis 작업을 위한 템플릿
     * @param tokenBucketScript 토큰 버킷 로직을 구현한 Lua 스크립트
     * @param meterRegistry 메트릭 수집을 위한 레지스트리
     */
    public TokenBucketRateLimiterService(RedisTemplate<String, Object> redisTemplate, 
                                        RedisScript<Boolean> tokenBucketScript,
                                        MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.tokenBucketScript = tokenBucketScript;
        this.meterRegistry = meterRegistry;
        
        // 메트릭 초기화
        this.totalRequestsCounter = Counter.builder("token_bucket_rate_limiter.requests.total")
                .description("토큰 버킷 속도 제한 요청 총 횟수")
                .register(meterRegistry);
                
        this.allowedRequestsCounter = Counter.builder("token_bucket_rate_limiter.requests.allowed")
                .description("토큰 버킷 속도 제한 내에서 허용된 요청 횟수")
                .register(meterRegistry);
                
        this.rejectedRequestsCounter = Counter.builder("token_bucket_rate_limiter.requests.rejected")
                .description("토큰 버킷 속도 제한을 초과하여 거부된 요청 횟수")
                .register(meterRegistry);
                
        this.rateLimitTimer = Timer.builder("token_bucket_rate_limiter.execution.time")
                .description("토큰 버킷 속도 제한 실행 시간")
                .register(meterRegistry);
    }

    /**
     * 주어진 키에 대한 요청이 속도 제한을 초과하는지 확인합니다.
     * Redis의 Lua 스크립트를 사용하여 토큰 버킷 알고리즘을 구현합니다.
     *
     * @param key 속도 제한을 적용할 고유 키 (예: 사용자 ID, IP 주소 등)
     * @param limit 버킷의 최대 토큰 수 (버킷 크기)
     * @param period 토큰이 완전히 리필되는 시간(초)
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
            
            // 키 이름 생성 (예: token_bucket:user_123)
            String redisKey = "token_bucket:" + key;
            
            // 토큰 리필 속도 계산 (토큰/밀리초)
            double refillRate = (double) limit / (period * 1000);
            
            // Lua 스크립트 실행에 필요한 키와 인자 준비
            List<String> keys = Collections.singletonList(redisKey);
            Object[] args = { now, limit, refillRate };
            
            // Lua 스크립트 실행 및 결과 저장
            boolean allowed = Boolean.TRUE.equals(redisTemplate.execute(tokenBucketScript, keys, args));
            
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