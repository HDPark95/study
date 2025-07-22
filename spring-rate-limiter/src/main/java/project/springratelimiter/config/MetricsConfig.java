package project.springratelimiter.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션 메트릭 설정을 위한 구성 클래스.
 * 이 클래스는 커스텀 메트릭 등록 및 설정을 담당합니다.
 */
@Configuration
public class MetricsConfig {

    /**
     * MeterRegistry 커스터마이징을 위한 빈.
     * 애플리케이션 시작 시 기본 태그 및 설정을 적용합니다.
     *
     * @param registry 자동 주입된 MeterRegistry
     * @return 커스터마이징된 MeterRegistry
     */
    @Bean
    public MeterRegistry meterRegistryCustomizer(MeterRegistry registry) {
        // 여기에서 필요한 경우 MeterRegistry를 커스터마이징할 수 있습니다.
        // 예: 공통 태그 추가, 기본 설정 변경 등
        return registry;
    }
}