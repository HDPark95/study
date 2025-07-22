# 속도 제한기(Rate Limiter) 모니터링 설정

이 문서는 Spring Boot 애플리케이션의 속도 제한기(Rate Limiter)에 대한 모니터링 설정을 설명합니다.

## 구성 요소

모니터링 시스템은 다음 구성 요소로 이루어져 있습니다:

1. **Spring Boot Actuator**: 애플리케이션 메트릭을 수집하고 노출합니다.
2. **Micrometer**: 애플리케이션 메트릭을 수집하는 라이브러리입니다.
3. **Prometheus**: 메트릭을 수집하고 저장하는 시계열 데이터베이스입니다.
4. **Grafana**: 메트릭을 시각화하고 알림을 설정하는 대시보드 도구입니다.

## 설정 방법

### 1. 의존성 추가

`build.gradle.kts` 파일에 다음 의존성을 추가했습니다:

```kotlin
dependencies {
    // 기존 의존성...
    
    // 모니터링을 위한 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
}
```

### 2. Spring Boot Actuator 설정

`application.yml` 파일에 다음 설정을 추가했습니다:

```yaml
# Actuator 설정
management:
  endpoints:
    web:
      exposure:
        include: "*"  # 모든 엔드포인트 노출 (프로덕션 환경에서는 필요한 엔드포인트만 노출하는 것이 좋음)
  endpoint:
    health:
      show-details: always  # 상세한 헬스 정보 표시
    prometheus:
      enabled: true  # Prometheus 엔드포인트 활성화
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true  # HTTP 요청에 대한 히스토그램 활성화
    tags:
      application: ${spring.application.name}  # 모든 메트릭에 애플리케이션 이름 태그 추가
```

### 3. 커스텀 메트릭 추가

`RedisRateLimiterService` 클래스에 다음 메트릭을 추가했습니다:

- `rate_limiter_requests_total`: 총 요청 수
- `rate_limiter_requests_allowed`: 허용된 요청 수
- `rate_limiter_requests_rejected`: 거부된 요청 수
- `rate_limiter_execution_time`: 속도 제한 실행 시간

### 4. Docker Compose 설정

`docker-compose.yml` 파일에 Prometheus와 Grafana 서비스를 추가했습니다:

```yaml
  # 프로메테우스 서비스 설정
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--web.enable-lifecycle'
    restart: always
  
  # 그라파나 서비스 설정
  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    volumes:
      - grafana_data:/var/lib/grafana
      - ./grafana/provisioning:/etc/grafana/provisioning
      - ./grafana/dashboards:/var/lib/grafana/dashboards
    depends_on:
      - prometheus
    restart: always
```

### 5. Prometheus 설정

`prometheus/prometheus.yml` 파일을 생성하여 다음과 같이 설정했습니다:

```yaml
global:
  scrape_interval: 15s     # 기본 스크랩 간격 (15초마다 메트릭 수집)
  evaluation_interval: 15s # 기본 평가 간격 (15초마다 규칙 평가)

# 스크랩 설정
scrape_configs:
  # Spring Boot 애플리케이션 메트릭 수집 설정
  - job_name: 'spring-rate-limiter'
    metrics_path: '/actuator/prometheus'  # Spring Boot Actuator의 Prometheus 엔드포인트
    scrape_interval: 5s  # 이 작업에 대한 스크랩 간격 (5초)
    static_configs:
      - targets: ['host.docker.internal:8080']  # Docker 내부에서 호스트 머신의 Spring Boot 애플리케이션에 접근

  # Prometheus 자체 메트릭 수집 설정
  - job_name: 'prometheus'
    scrape_interval: 15s
    static_configs:
      - targets: ['localhost:9090']
```

### 6. Grafana 대시보드 설정

Grafana 대시보드를 자동으로 설정하기 위해 다음 파일들을 생성했습니다:

1. `grafana/provisioning/datasources/prometheus.yml`: Prometheus 데이터 소스 설정
2. `grafana/provisioning/dashboards/dashboards.yml`: 대시보드 프로바이더 설정
3. `grafana/dashboards/rate-limiter-dashboard.json`: 속도 제한기 대시보드 설정
4. `grafana/provisioning/alerting/rate_limiter_alerts.yml`: 알림 규칙 설정

## 모니터링 시스템 사용 방법

### 1. 시스템 시작

다음 명령어로 모니터링 시스템을 시작합니다:

```bash
docker-compose up -d
```

### 2. 접근 방법

- **Spring Boot Actuator**: http://localhost:8080/actuator
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (기본 계정: admin/admin)

### 3. 대시보드 확인

Grafana에 로그인한 후, "Rate Limiter" 폴더에서 "Rate Limiter 모니터링" 대시보드를 확인할 수 있습니다.

### 4. 알림 확인

Grafana의 알림 섹션에서 "높은 요청 거부율" 알림 규칙을 확인할 수 있습니다. 이 알림은 요청 거부율이 20%를 초과할 경우 트리거됩니다.

## 테스트 방법

다음 API 엔드포인트를 사용하여 속도 제한기를 테스트하고 모니터링 시스템이 올바르게 작동하는지 확인할 수 있습니다:

- **기본 속도 제한 (10 요청/60초)**: http://localhost:8080/api/default
- **사용자 지정 속도 제한 (5 요청/30초)**: http://localhost:8080/api/custom
- **메서드 기반 속도 제한 (20 요청/60초)**: http://localhost:8080/api/method
- **속도 제한 없음**: http://localhost:8080/api/unlimited

다음 명령어를 사용하여 부하 테스트를 수행할 수 있습니다:

```bash
# 기본 속도 제한 엔드포인트에 20개의 요청 전송
for i in {1..20}; do curl http://localhost:8080/api/default; echo ""; done
```

이 테스트를 실행한 후 Grafana 대시보드에서 메트릭을 확인하면, 처음 10개의 요청은 허용되고 나머지 10개는 거부되는 것을 볼 수 있습니다.