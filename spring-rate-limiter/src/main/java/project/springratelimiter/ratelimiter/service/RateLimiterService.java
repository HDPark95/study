package project.springratelimiter.ratelimiter.service;

/**
 * 요청 속도 제한을 위한 서비스 인터페이스.
 * 이 인터페이스는 API 요청의 속도를 제한하는 메서드를 정의합니다.
 */
public interface RateLimiterService {

    /**
     * 주어진 키에 대한 요청이 속도 제한을 초과하는지 확인합니다.
     *
     * @param key 속도 제한을 적용할 고유 키 (예: 사용자 ID, IP 주소 등)
     * @param limit 허용된 요청 수
     * @param period 시간 기간(초)
     * @return 요청이 속도 제한 내에 있으면 true, 그렇지 않으면 false
     */
    boolean tryAcquire(String key, long limit, long period);
}