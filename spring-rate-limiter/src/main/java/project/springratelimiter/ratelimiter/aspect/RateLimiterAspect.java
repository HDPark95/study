package project.springratelimiter.ratelimiter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import project.springratelimiter.ratelimiter.annotation.RateLimit;
import project.springratelimiter.ratelimiter.exception.RateLimitExceededException;
import project.springratelimiter.ratelimiter.factory.RateLimiterFactory;
import project.springratelimiter.ratelimiter.service.RateLimiterService;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @RateLimit 어노테이션이 적용된 메서드에 대한 요청을 가로채는 Aspect.
 * 이 Aspect는 요청이 속도 제한을 초과하는지 확인하고, 초과할 경우 예외를 발생시킵니다.
 */
@Aspect
@Component
public class RateLimiterAspect {

    private final RateLimiterFactory rateLimiterFactory;

    /**
     * RateLimiterFactory를 사용하여 RateLimiterAspect를 생성합니다.
     *
     * @param rateLimiterFactory 속도 제한 알고리즘에 따라 적절한 서비스를 제공하는 팩토리
     */
    public RateLimiterAspect(RateLimiterFactory rateLimiterFactory) {
        this.rateLimiterFactory = rateLimiterFactory;
    }

    /**
     * @RateLimit 어노테이션이 적용된 메서드를 가로채고 속도 제한을 적용합니다.
     *
     * @param joinPoint 가로챈 메서드의 조인 포인트
     * @return 원래 메서드의 결과
     * @throws Throwable 원래 메서드에서 발생한 예외 또는 속도 제한 초과 예외
     */
    @Around("@annotation(project.springratelimiter.ratelimiter.annotation.RateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        // 메서드 시그니처 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // @RateLimit 어노테이션 가져오기
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        // 속도 제한 파라미터 추출
        long limit = rateLimit.limit();
        long period = rateLimit.period();
        RateLimit.KeyType keyType = rateLimit.keyType();
        RateLimit.Algorithm algorithm = rateLimit.algorithm();

        // 키 생성
        String key = generateKey(method, keyType);

        // 알고리즘에 맞는 RateLimiterService 가져오기
        RateLimiterService rateLimiterService = rateLimiterFactory.getRateLimiter(algorithm);

        // 속도 제한 확인
        boolean allowed = rateLimiterService.tryAcquire(key, limit, period);

        // 속도 제한 초과 시 예외 발생
        if (!allowed) {
            throw new RateLimitExceededException(
                    String.format("속도 제한 초과: %d 요청/%d초 (알고리즘: %s)", limit, period, algorithm));
        }

        // 원래 메서드 실행
        return joinPoint.proceed();
    }

    /**
     * 속도 제한에 사용할 키를 생성합니다.
     *
     * @param method 가로챈 메서드
     * @param keyType 키 유형
     * @return 생성된 키
     */
    private String generateKey(Method method, RateLimit.KeyType keyType) {
        switch (keyType) {
            case IP:
                return getClientIp();
            case USER:
                return getUserId();
            case METHOD:
                return method.getDeclaringClass().getName() + "." + method.getName();
            default:
                return getClientIp();
        }
    }

    /**
     * 클라이언트 IP 주소를 가져옵니다.
     *
     * @return 클라이언트 IP 주소
     */
    private String getClientIp() {
        HttpServletRequest request = getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        return ip;
    }

    /**
     * 현재 사용자 ID를 가져옵니다.
     * 이 메서드는 실제 인증 구현에 맞게 수정해야 합니다.
     *
     * @return 사용자 ID 또는 기본값
     */
    private String getUserId() {
        // 실제 구현에서는 Spring Security의 Authentication에서 사용자 ID를 가져와야 합니다.
        // 이 예제에서는 간단히 "anonymous"를 반환합니다.
        return "anonymous";
    }

    /**
     * 현재 HTTP 요청을 가져옵니다.
     *
     * @return HTTP 요청
     */
    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
    }
}