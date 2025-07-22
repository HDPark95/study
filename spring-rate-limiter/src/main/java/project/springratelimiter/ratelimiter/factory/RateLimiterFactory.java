package project.springratelimiter.ratelimiter.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import project.springratelimiter.ratelimiter.annotation.RateLimit;
import project.springratelimiter.ratelimiter.annotation.RateLimiterType;
import project.springratelimiter.ratelimiter.service.RateLimiterService;

import java.util.HashMap;
import java.util.Map;

/**
 * 속도 제한 알고리즘에 따라 적절한 RateLimiterService 구현체를 제공하는 팩토리 클래스.
 * 이 클래스는 알고리즘 유형에 따라 적절한 서비스 인스턴스를 반환합니다.
 */
@Component
public class RateLimiterFactory {

    private final Map<RateLimit.Algorithm, RateLimiterService> rateLimiters = new HashMap<>();

    /**
     * Spring ApplicationContext를 사용하여 모든 RateLimiterService 구현체를 찾아 맵에 저장합니다.
     * 
     * @param applicationContext Spring ApplicationContext
     */
    @Autowired
    public RateLimiterFactory(ApplicationContext applicationContext) {
        // @RateLimiterType 어노테이션이 있는 모든 RateLimiterService 빈을 찾아 맵에 저장
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RateLimiterType.class);
        
        for (Object bean : beans.values()) {
            if (bean instanceof RateLimiterService) {
                RateLimiterType annotation = bean.getClass().getAnnotation(RateLimiterType.class);
                rateLimiters.put(annotation.value(), (RateLimiterService) bean);
            }
        }
    }

    /**
     * 지정된 알고리즘 유형에 맞는 RateLimiterService 구현체를 반환합니다.
     * 
     * @param algorithm 사용할 속도 제한 알고리즘
     * @return 해당 알고리즘을 구현한 RateLimiterService
     * @throws IllegalArgumentException 지원되지 않는 알고리즘이 지정된 경우
     */
    public RateLimiterService getRateLimiter(RateLimit.Algorithm algorithm) {
        RateLimiterService rateLimiter = rateLimiters.get(algorithm);
        
        if (rateLimiter == null) {
            throw new IllegalArgumentException("지원되지 않는 속도 제한 알고리즘입니다: " + algorithm);
        }
        
        return rateLimiter;
    }
}