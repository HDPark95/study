package project.springratelimiter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 애플리케이션을 위한 Redis 설정 클래스.
 * 이 클래스는 Redis 연결 팩토리와 Redis 템플릿 빈을 구성합니다.
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    /**
     * Lettuce 드라이버를 사용하여 Redis 연결 팩토리를 생성합니다.
     * 
     * @return application.yml에 구성된 호스트와 포트로 설정된 RedisConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(redisConfig);
    }

    /**
     * String 키와 JSON 직렬화된 값을 가진 Redis 템플릿을 생성합니다.
     * 
     * @param connectionFactory Redis 연결 팩토리
     * @return 적절한 직렬화 도구로 구성된 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 키 직렬화 도구 설정
        template.setKeySerializer(new StringRedisSerializer());

        // 값 직렬화 도구 설정
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 해시 키와 값 직렬화 도구 설정
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}