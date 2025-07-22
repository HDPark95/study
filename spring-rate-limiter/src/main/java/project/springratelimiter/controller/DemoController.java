package project.springratelimiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.springratelimiter.ratelimiter.annotation.RateLimit;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 속도 제한 기능을 시연하기 위한 데모 컨트롤러.
 * 이 컨트롤러는 다양한 속도 제한 설정과 알고리즘으로 여러 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    /**
     * 기본 속도 제한 설정(10 요청/60초)이 적용된 엔드포인트.
     * 기본 알고리즘(슬라이딩 윈도우)을 사용하며, IP 주소별로 속도 제한이 적용됩니다.
     *
     * @return 현재 시간이 포함된 응답
     */
    @GetMapping("/default")
    @RateLimit
    public ResponseEntity<Map<String, Object>> defaultRateLimit() {
        return createResponse("기본 속도 제한 (10 요청/60초, 슬라이딩 윈도우 알고리즘)");
    }

    /**
     * 사용자 지정 속도 제한 설정(5 요청/30초)이 적용된 엔드포인트.
     * 기본 알고리즘(슬라이딩 윈도우)을 사용하며, IP 주소별로 속도 제한이 적용됩니다.
     *
     * @return 현재 시간이 포함된 응답
     */
    @GetMapping("/custom")
    @RateLimit(limit = 5, period = 30)
    public ResponseEntity<Map<String, Object>> customRateLimit() {
        return createResponse("사용자 지정 속도 제한 (5 요청/30초, 슬라이딩 윈도우 알고리즘)");
    }

    /**
     * 메서드 기반 속도 제한이 적용된 엔드포인트.
     * 기본 알고리즘(슬라이딩 윈도우)을 사용하며, 모든 사용자에게 공통으로 속도 제한이 적용됩니다.
     *
     * @return 현재 시간이 포함된 응답
     */
    @GetMapping("/method")
    @RateLimit(keyType = RateLimit.KeyType.METHOD, limit = 20, period = 60)
    public ResponseEntity<Map<String, Object>> methodRateLimit() {
        return createResponse("메서드 기반 속도 제한 (20 요청/60초, 슬라이딩 윈도우 알고리즘, 모든 사용자 공통)");
    }

    /**
     * 고정 윈도우 알고리즘을 사용한 속도 제한이 적용된 엔드포인트.
     * 이 알고리즘은 시간을 고정된 윈도우로 나누고 각 윈도우 내의 요청 수를 제한합니다.
     * 구현이 간단하지만 윈도우 경계에서 트래픽 급증이 발생할 수 있습니다.
     *
     * @return 현재 시간이 포함된 응답
     */
    @GetMapping("/fixed-window")
    @RateLimit(algorithm = RateLimit.Algorithm.FIXED_WINDOW, limit = 5, period = 30)
    public ResponseEntity<Map<String, Object>> fixedWindowRateLimit() {
        return createResponse("고정 윈도우 속도 제한 (5 요청/30초)");
    }

    /**
     * 토큰 버킷 알고리즘을 사용한 속도 제한이 적용된 엔드포인트.
     * 이 알고리즘은 일정 속도로 토큰을 생성하고 각 요청마다 토큰을 소비합니다.
     * 버킷 크기까지 토큰을 저장할 수 있어 일시적인 트래픽 급증을 허용합니다.
     *
     * @return 현재 시간이 포함된 응답
     */
    @GetMapping("/token-bucket")
    @RateLimit(algorithm = RateLimit.Algorithm.TOKEN_BUCKET, limit = 5, period = 30)
    public ResponseEntity<Map<String, Object>> tokenBucketRateLimit() {
        return createResponse("토큰 버킷 속도 제한 (5 요청/30초)");
    }

    /**
     * 누수 버킷 알고리즘을 사용한 속도 제한이 적용된 엔드포인트.
     * 이 알고리즘은 일정한 속도로 요청을 처리하고 초과 요청은 대기열에 넣거나 거부합니다.
     * 일정한 처리 속도를 보장하지만 버스트 트래픽을 처리하는 데 제한이 있습니다.
     *
     * @return 현재 시간이 포함된 응답
     */
    @GetMapping("/leaky-bucket")
    @RateLimit(algorithm = RateLimit.Algorithm.LEAKY_BUCKET, limit = 5, period = 30)
    public ResponseEntity<Map<String, Object>> leakyBucketRateLimit() {
        return createResponse("누수 버킷 속도 제한 (5 요청/30초)");
    }
    
    /**
     * 슬라이딩 윈도우 카운터 알고리즘을 사용한 속도 제한이 적용된 엔드포인트.
     * 이 알고리즘은 시간을 작은 버킷으로 나누고 각 버킷의 요청 수를 저장합니다.
     * 현재 윈도우와 이전 윈도우의 가중 합계를 사용하여 속도 제한을 계산합니다.
     * 슬라이딩 윈도우 로그보다 메모리 효율적이며 여전히 윈도우 경계에서 트래픽 급증을 방지합니다.
     *
     * @return 현재 시간이 포함된 응답
     */
    @GetMapping("/sliding-window-counter")
    @RateLimit(algorithm = RateLimit.Algorithm.SLIDING_WINDOW_COUNTER, limit = 5, period = 30)
    public ResponseEntity<Map<String, Object>> slidingWindowCounterRateLimit() {
        return createResponse("슬라이딩 윈도우 카운터 속도 제한 (5 요청/30초)");
    }

    /**
     * 속도 제한이 적용되지 않은 엔드포인트.
     *
     * @return 현재 시간이 포함된 응답
     */
    @GetMapping("/unlimited")
    public ResponseEntity<Map<String, Object>> unlimited() {
        return createResponse("속도 제한 없음");
    }

    /**
     * 응답 데이터를 생성합니다.
     *
     * @param message 응답에 포함할 메시지
     * @return 응답 데이터가 포함된 ResponseEntity
     */
    private ResponseEntity<Map<String, Object>> createResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", message);
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
}