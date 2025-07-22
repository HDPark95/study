package project.springratelimiter.ratelimiter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 요청이 속도 제한을 초과할 때 발생하는 예외.
 * 이 예외는 HTTP 상태 코드 429 (Too Many Requests)를 반환합니다.
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException {

    /**
     * 기본 메시지로 예외를 생성합니다.
     */
    public RateLimitExceededException() {
        super("요청 속도 제한을 초과했습니다. 잠시 후 다시 시도해주세요.");
    }

    /**
     * 지정된 메시지로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public RateLimitExceededException(String message) {
        super(message);
    }

    /**
     * 지정된 메시지와 원인으로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 예외의 원인
     */
    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}