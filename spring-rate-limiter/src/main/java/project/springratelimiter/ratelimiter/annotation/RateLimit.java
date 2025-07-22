package project.springratelimiter.ratelimiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드에 속도 제한을 적용하기 위한 어노테이션.
 * 이 어노테이션은 컨트롤러 메서드에 적용하여 요청 속도를 제한할 수 있습니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * 지정된 기간 내에 허용되는 요청 수
     * 
     * @return 허용되는 요청 수
     */
    long limit() default 10;
    
    /**
     * 속도 제한이 적용되는 시간 기간(초)
     * 
     * @return 시간 기간(초)
     */
    long period() default 60;
    
    /**
     * 속도 제한에 사용할 키 유형
     * 
     * @return 키 유형
     */
    KeyType keyType() default KeyType.IP;
    
    /**
     * 속도 제한에 사용할 알고리즘
     * 
     * @return 알고리즘 유형
     */
    Algorithm algorithm() default Algorithm.SLIDING_WINDOW;
    
    /**
     * 속도 제한 키를 생성하는 데 사용할 수 있는 키 유형
     */
    enum KeyType {
        /**
         * 클라이언트 IP 주소를 키로 사용
         */
        IP,
        
        /**
         * 사용자 ID를 키로 사용 (인증된 사용자에게만 적용 가능)
         */
        USER,
        
        /**
         * 메서드 이름을 키로 사용 (모든 사용자에게 공통으로 적용)
         */
        METHOD
    }
    
    /**
     * 속도 제한에 사용할 수 있는 알고리즘 유형
     */
    enum Algorithm {
        /**
         * 슬라이딩 윈도우 로그 알고리즘 - 지정된 시간 범위 내의 모든 요청을 개별적으로 추적하여 정확한 속도 제한을 제공합니다.
         * 각 요청을 타임스탬프와 함께 저장하고 만료된 요청을 제거합니다.
         * 윈도우 경계에서 트래픽 급증을 방지하지만 요청이 많을 경우 메모리 사용량이 증가할 수 있습니다.
         */
        SLIDING_WINDOW,
        
        /**
         * 슬라이딩 윈도우 카운터 알고리즘 - 시간을 작은 버킷으로 나누고 각 버킷의 요청 수를 저장합니다.
         * 현재 윈도우와 이전 윈도우의 가중 합계를 사용하여 속도 제한을 계산합니다.
         * 슬라이딩 윈도우 로그보다 메모리 효율적이며 여전히 윈도우 경계에서 트래픽 급증을 방지합니다.
         */
        SLIDING_WINDOW_COUNTER,
        
        /**
         * 고정 윈도우 카운터 알고리즘 - 지정된 시간 범위를 고정된 윈도우로 나누어 각 윈도우 내의 요청 수를 제한합니다.
         * 구현이 간단하지만 윈도우 경계에서 트래픽 급증이 발생할 수 있습니다.
         */
        FIXED_WINDOW,
        
        /**
         * 토큰 버킷 알고리즘 - 일정 속도로 토큰을 생성하고 각 요청마다 토큰을 소비합니다.
         * 버킷 크기까지 토큰을 저장할 수 있어 일시적인 트래픽 급증을 허용합니다.
         */
        TOKEN_BUCKET,
        
        /**
         * 누수 버킷 알고리즘 - 일정한 속도로 요청을 처리하고 초과 요청은 대기열에 넣거나 거부합니다.
         * 일정한 처리 속도를 보장하지만 버스트 트래픽을 처리하는 데 제한이 있습니다.
         */
        LEAKY_BUCKET
    }
}