package project.springratelimiter.ratelimiter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 애플리케이션 전체에서 발생하는 예외를 처리하는 글로벌 예외 핸들러.
 * 이 클래스는 일관된 오류 응답 형식을 제공합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * RateLimitExceededException을 처리하고 적절한 오류 응답을 반환합니다.
     *
     * @param ex 처리할 예외
     * @return 오류 정보가 포함된 ResponseEntity
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExceededException(RateLimitExceededException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        errorResponse.put("error", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }
}