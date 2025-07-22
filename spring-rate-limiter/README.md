# Spring Rate Limiter

Spring Boot 애플리케이션에서 Redis를 사용한 API 요청 속도 제한(Rate Limiting) 구현 예제입니다.

## 개요

이 프로젝트는 Spring Boot와 Redis를 사용하여 API 요청 속도 제한을 구현하는 방법을 보여줍니다. 속도 제한은 API의 안정성을 유지하고, 서비스 거부 공격(DoS)을 방지하며, 공정한 리소스 사용을 보장하는 데 중요합니다.

## 기능

- 다양한 속도 제한 알고리즘 구현:
  - 슬라이딩 윈도우 로그 (Sliding Window Log)
  - 슬라이딩 윈도우 카운터 (Sliding Window Counter)
  - 고정 윈도우 카운터 (Fixed Window Counter)
  - 토큰 버킷 (Token Bucket)
  - 누수 버킷 (Leaky Bucket)
- 어노테이션 기반 속도 제한 적용 (`@RateLimit`)
- 다양한 키 유형 지원 (IP 주소, 사용자 ID, 메서드 이름)
- 사용자 정의 속도 제한 매개변수 (제한 횟수, 시간 기간)
- 속도 제한 초과 시 적절한 오류 응답 (HTTP 429 Too Many Requests)

## 기술 스택

- Java 24
- Spring Boot 3.5.3
- Spring AOP
- Spring Data Redis
- Redis
- Docker & Docker Compose
- JUnit 5 & Testcontainers

## 시작하기

### 필수 조건

- Java 24 이상
- Docker 및 Docker Compose
- Maven 또는 Gradle

### 설치 및 실행

1. 저장소 복제:
   ```bash
   git clone https://github.com/yourusername/spring-rate-limiter.git
   cd spring-rate-limiter
   ```

2. Docker Compose로 Redis 및 MySQL 시작:
   ```bash
   docker-compose up -d
   ```

3. 애플리케이션 실행:
   ```bash
   ./gradlew bootRun
   ```

## 사용 방법

### 속도 제한 어노테이션 적용

컨트롤러 메서드에 `@RateLimit` 어노테이션을 추가하여 속도 제한을 적용할 수 있습니다:

```java
@GetMapping("/api/resource")
@RateLimit(
    limit = 10,                              // 허용되는 요청 수
    period = 60,                             // 시간 기간(초)
    keyType = RateLimit.KeyType.IP,          // 키 유형
    algorithm = RateLimit.Algorithm.TOKEN_BUCKET  // 속도 제한 알고리즘
)
public ResponseEntity<Resource> getResource() {
    // 메서드 구현
}
```

### 속도 제한 매개변수

- `limit`: 지정된 기간 내에 허용되는 요청 수 (기본값: 10)
- `period`: 속도 제한이 적용되는 시간 기간(초) (기본값: 60)
- `keyType`: 속도 제한에 사용할 키 유형 (기본값: IP)
  - `IP`: 클라이언트 IP 주소를 키로 사용
  - `USER`: 사용자 ID를 키로 사용 (인증된 사용자에게만 적용 가능)
  - `METHOD`: 메서드 이름을 키로 사용 (모든 사용자에게 공통으로 적용)
- `algorithm`: 속도 제한에 사용할 알고리즘 (기본값: SLIDING_WINDOW)
  - `SLIDING_WINDOW`: 슬라이딩 윈도우 로그 알고리즘
  - `SLIDING_WINDOW_COUNTER`: 슬라이딩 윈도우 카운터 알고리즘
  - `FIXED_WINDOW`: 고정 윈도우 카운터 알고리즘
  - `TOKEN_BUCKET`: 토큰 버킷 알고리즘
  - `LEAKY_BUCKET`: 누수 버킷 알고리즘

## 속도 제한 알고리즘

### 슬라이딩 윈도우 로그 (Sliding Window Log)

슬라이딩 윈도우 로그 알고리즘은 지정된 시간 범위 내의 모든 요청을 개별적으로 추적하여 정확한 속도 제한을 제공합니다. 각 요청을 타임스탬프와 함께 저장하고 만료된 요청을 제거합니다. 이 알고리즘은 윈도우 경계에서 트래픽 급증을 방지하지만 요청이 많을 경우 메모리 사용량이 증가할 수 있습니다.

```java
@RateLimit(algorithm = RateLimit.Algorithm.SLIDING_WINDOW, limit = 10, period = 60)
```

### 슬라이딩 윈도우 카운터 (Sliding Window Counter)

슬라이딩 윈도우 카운터 알고리즘은 시간을 작은 버킷으로 나누고 각 버킷의 요청 수를 저장합니다. 현재 윈도우와 이전 윈도우의 가중 합계를 사용하여 속도 제한을 계산합니다. 이 방식은 슬라이딩 윈도우 로그보다 메모리 효율적이며 여전히 윈도우 경계에서 트래픽 급증을 방지합니다.

```java
@RateLimit(algorithm = RateLimit.Algorithm.SLIDING_WINDOW_COUNTER, limit = 10, period = 60)
```

### 고정 윈도우 카운터 (Fixed Window Counter)

고정 윈도우 카운터 알고리즘은 시간을 고정된 윈도우로 나누고 각 윈도우 내의 요청 수를 제한합니다. 구현이 간단하지만 윈도우 경계에서 트래픽 급증이 발생할 수 있습니다.

```java
@RateLimit(algorithm = RateLimit.Algorithm.FIXED_WINDOW, limit = 10, period = 60)
```

### 토큰 버킷 (Token Bucket)

토큰 버킷 알고리즘은 일정 속도로 토큰을 생성하고 각 요청마다 토큰을 소비합니다. 버킷 크기까지 토큰을 저장할 수 있어 일시적인 트래픽 급증을 허용합니다.

```java
@RateLimit(algorithm = RateLimit.Algorithm.TOKEN_BUCKET, limit = 10, period = 60)
```

### 누수 버킷 (Leaky Bucket)

누수 버킷 알고리즘은 일정한 속도로 요청을 처리하고 초과 요청은 대기열에 넣거나 거부합니다. 일정한 처리 속도를 보장하지만 버스트 트래픽을 처리하는 데 제한이 있습니다.

```java
@RateLimit(algorithm = RateLimit.Algorithm.LEAKY_BUCKET, limit = 10, period = 60)
```

## 데모 엔드포인트

애플리케이션에는 속도 제한 기능을 시연하기 위한 여러 엔드포인트가 포함되어 있습니다:

- `GET /api/default`: 기본 속도 제한 설정(10 요청/60초, 슬라이딩 윈도우 로그 알고리즘)
- `GET /api/custom`: 사용자 지정 속도 제한 설정(5 요청/30초, 슬라이딩 윈도우 로그 알고리즘)
- `GET /api/method`: 메서드 기반 속도 제한(20 요청/60초, 슬라이딩 윈도우 로그 알고리즘, 모든 사용자 공통)
- `GET /api/sliding-window-counter`: 슬라이딩 윈도우 카운터 알고리즘 속도 제한(5 요청/30초)
- `GET /api/fixed-window`: 고정 윈도우 알고리즘 속도 제한(5 요청/30초)
- `GET /api/token-bucket`: 토큰 버킷 알고리즘 속도 제한(5 요청/30초)
- `GET /api/leaky-bucket`: 누수 버킷 알고리즘 속도 제한(5 요청/30초)
- `GET /api/unlimited`: 속도 제한 없음

## 테스트

단위 테스트 및 통합 테스트 실행:

```bash
./gradlew test
```

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.