package project.springratelimiter.ratelimiter.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 속도 제한 서비스 구현체가 지원하는 알고리즘 유형을 지정하는 어노테이션.
 * 이 어노테이션은 Spring의 의존성 주입 시스템에서 적절한 RateLimiterService 구현체를 식별하는 데 사용됩니다.
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface RateLimiterType {
    
    /**
     * 이 서비스 구현체가 지원하는 속도 제한 알고리즘
     * 
     * @return 지원하는 알고리즘 유형
     */
    RateLimit.Algorithm value();
}