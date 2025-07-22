package project.springratelimiter.ratelimiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * 속도 제한 기능을 위한 설정 클래스.
 * 이 클래스는 Redis Lua 스크립트를 정의하고 빈으로 등록합니다.
 */
@Configuration
public class RateLimiterConfig {

    /**
     * 슬라이딩 윈도우 알고리즘을 구현한 Lua 스크립트를 RedisScript 빈으로 등록합니다.
     * 
     * 슬라이딩 윈도우 알고리즘은 지정된 시간 범위 내의 모든 요청을 추적하여 정확한 속도 제한을 제공합니다.
     * Redis의 Sorted Set을 사용하여 타임스탬프가 있는 요청을 저장하고, 만료된 요청을 제거합니다.
     * 이 방식은 고정 윈도우와 달리 시간 경계에서 트래픽 급증을 방지하는 장점이 있습니다.
     * 
     * @return 슬라이딩 윈도우 속도 제한 로직을 수행하는 RedisScript
     */
    @Bean
    public RedisScript<Boolean> rateLimiterScript() {
        // 인라인 Lua 스크립트 정의 - 텍스트 블록 사용
        String script = """
            -- 입력 파라미터 추출
            local key = KEYS[1]                -- Redis 키 (사용자 ID, IP 등)
            local now = tonumber(ARGV[1])      -- 현재 시간 (밀리초)
            local limit = tonumber(ARGV[2])    -- 허용된 최대 요청 수
            local period = tonumber(ARGV[3])   -- 시간 기간 (밀리초)
            
            -- 만료 시간 계산 (현재 시간 - 기간)
            -- 이 시간보다 이전의 요청은 모두 만료된 것으로 간주
            local expired = now - period
            
            -- 만료된 요청 제거 (Sorted Set에서 점수가 0부터 expired까지인 모든 요소 제거)
            -- ZREMRANGEBYSCORE: 지정된 점수 범위의 모든 요소를 제거하는 Redis 명령어
            redis.call('ZREMRANGEBYSCORE', key, 0, expired)
            
            -- 현재 요청 수 계산 (Sorted Set의 요소 수 조회)
            -- ZCARD: Sorted Set의 요소 수를 반환하는 Redis 명령어
            local count = redis.call('ZCARD', key)
            
            -- 요청 수가 제한보다 작으면 요청 추가
            if count < limit then
              -- 현재 요청을 Sorted Set에 추가 (점수는 현재 시간, 값은 고유한 식별자)
              -- ZADD: Sorted Set에 요소를 추가하는 Redis 명령어
              redis.call('ZADD', key, now, now .. '-' .. math.random())
              
              -- 키 만료 시간 설정 (초 단위로 변환)
              -- EXPIRE: 키의 만료 시간을 설정하는 Redis 명령어
              redis.call('EXPIRE', key, math.ceil(period/1000))
              
              -- 요청 허용
              return true
            end
            
            -- 요청 수가 제한 이상이면 요청 거부
            return false
            """;
        
        return RedisScript.of(script, Boolean.class);
    }
    
    /**
     * 토큰 버킷 알고리즘을 구현한 Lua 스크립트를 RedisScript 빈으로 등록합니다.
     * 
     * 토큰 버킷 알고리즘은 일정 속도로 토큰을 생성하고 각 요청마다 토큰을 소비하는 방식입니다.
     * 버킷에는 최대 용량까지 토큰을 저장할 수 있어 일시적인 트래픽 급증을 허용합니다.
     * 이 알고리즘은 평균 요청 속도를 제한하면서도 버스트 트래픽을 처리할 수 있는 유연성을 제공합니다.
     * Redis의 Hash 자료구조를 사용하여 토큰 수와 마지막 리필 시간을 저장합니다.
     * 
     * @return 토큰 버킷 속도 제한 로직을 수행하는 RedisScript
     */
    @Bean
    public RedisScript<Boolean> tokenBucketScript() {
        // 인라인 Lua 스크립트 정의 - 텍스트 블록 사용
        String script = """
            -- 입력 파라미터 추출
            local key = KEYS[1]                -- Redis 키 (사용자 ID, IP 등)
            local now = tonumber(ARGV[1])      -- 현재 시간 (밀리초)
            local capacity = tonumber(ARGV[2]) -- 버킷의 최대 용량 (최대 토큰 수)
            local refillRate = tonumber(ARGV[3]) -- 토큰 리필 속도 (토큰/밀리초)
            
            -- 버킷 정보 가져오기 (토큰 수, 마지막 리필 시간)
            -- HMGET: Hash에서 여러 필드의 값을 가져오는 Redis 명령어
            local bucket = redis.call('HMGET', key, 'tokens', 'lastRefill')
            local tokens = tonumber(bucket[1])
            local lastRefill = tonumber(bucket[2])
            
            -- 버킷이 없으면 새로 생성 (첫 요청 시)
            if tokens == nil or lastRefill == nil then
              -- 버킷을 최대 용량으로 초기화
              tokens = capacity
              lastRefill = now
            else
              -- 마지막 리필 이후 경과 시간 계산 (밀리초)
              local elapsed = now - lastRefill
              
              -- 경과 시간 동안 생성된 토큰 수 계산 및 최대 용량 제한
              -- math.min: 두 값 중 작은 값을 반환하여 최대 용량을 초과하지 않도록 함
              local newTokens = math.min(capacity, tokens + (elapsed * refillRate))
              tokens = newTokens
              lastRefill = now
            end
            
            -- 토큰이 있으면 하나 소비하고 요청 허용
            if tokens >= 1 then
              -- 토큰 하나 소비
              tokens = tokens - 1
              
              -- 버킷 정보 업데이트 (토큰 수, 마지막 리필 시간)
              -- HMSET: Hash에 여러 필드-값 쌍을 설정하는 Redis 명령어
              redis.call('HMSET', key, 'tokens', tokens, 'lastRefill', lastRefill)
              
              -- 키 만료 시간 설정 (1시간)
              -- EXPIRE: 키의 만료 시간을 설정하는 Redis 명령어
              redis.call('EXPIRE', key, 3600)
              
              -- 요청 허용
              return true
            end
            
            -- 토큰이 없어도 버킷 정보는 업데이트 (다음 요청을 위해)
            redis.call('HMSET', key, 'tokens', tokens, 'lastRefill', lastRefill)
            redis.call('EXPIRE', key, 3600)
            
            -- 토큰이 없으면 요청 거부
            return false
            """;
        
        return RedisScript.of(script, Boolean.class);
    }
    
    /**
     * 누수 버킷 알고리즘을 구현한 Lua 스크립트를 RedisScript 빈으로 등록합니다.
     * 
     * 누수 버킷 알고리즘은 일정한 속도로 요청을 처리하고 초과 요청은 대기열에 넣거나 거부하는 방식입니다.
     * 물이 일정한 속도로 새는 구멍이 있는 버킷을 상상하면 이해하기 쉽습니다.
     * 요청은 버킷에 물방울로 들어오고, 버킷에서는 일정한 속도로 요청이 처리됩니다.
     * 이 알고리즘은 일정한 처리 속도를 보장하여 백엔드 시스템을 안정적으로 보호합니다.
     * Redis의 Hash 자료구조를 사용하여 마지막 처리 시간과 대기열 크기를 저장합니다.
     * 
     * @return 누수 버킷 속도 제한 로직을 수행하는 RedisScript
     */
    @Bean
    public RedisScript<Boolean> leakyBucketScript() {
        // 인라인 Lua 스크립트 정의 - 텍스트 블록 사용
        String script = """
            -- 입력 파라미터 추출
            local key = KEYS[1]                -- Redis 키 (사용자 ID, IP 등)
            local now = tonumber(ARGV[1])      -- 현재 시간 (밀리초)
            local capacity = tonumber(ARGV[2]) -- 버킷의 최대 용량 (최대 대기열 크기)
            local rate = tonumber(ARGV[3])     -- 처리 속도 (요청/초)
            
            -- 버킷 정보 가져오기 (마지막 처리 시간, 대기열 크기)
            -- HMGET: Hash에서 여러 필드의 값을 가져오는 Redis 명령어
            local bucket = redis.call('HMGET', key, 'lastProcess', 'queue')
            local lastProcess = tonumber(bucket[1])
            local queue = tonumber(bucket[2])
            
            -- 버킷이 없으면 새로 생성 (첫 요청 시)
            if lastProcess == nil or queue == nil then
              -- 버킷을 초기화 (빈 대기열, 현재 시간)
              lastProcess = now
              queue = 0
            else
              -- 마지막 처리 이후 경과 시간 계산 (초)
              local elapsed = (now - lastProcess) / 1000
              
              -- 경과 시간 동안 처리된 요청 수 계산
              -- math.floor: 소수점 이하를 버림하여 정수 값을 얻음
              local processed = math.floor(elapsed * rate)
              
              -- 대기열에서 처리된 요청 수만큼 제거 (최소 0)
              -- math.max: 두 값 중 큰 값을 반환하여 대기열이 음수가 되지 않도록 함
              queue = math.max(0, queue - processed)
              
              -- 마지막 처리 시간 업데이트 (현재 대기열 기준)
              -- 대기열의 요청이 모두 처리되는 데 필요한 시간을 고려
              lastProcess = now - ((queue / rate) * 1000)
            end
            
            -- 대기열에 여유가 있으면 요청 추가
            if queue < capacity then
              -- 대기열에 요청 추가
              queue = queue + 1
              
              -- 버킷 정보 업데이트 (마지막 처리 시간, 대기열 크기)
              -- HMSET: Hash에 여러 필드-값 쌍을 설정하는 Redis 명령어
              redis.call('HMSET', key, 'lastProcess', lastProcess, 'queue', queue)
              
              -- 키 만료 시간 설정 (1시간)
              -- EXPIRE: 키의 만료 시간을 설정하는 Redis 명령어
              redis.call('EXPIRE', key, 3600)
              
              -- 요청 허용
              return true
            end
            
            -- 대기열이 가득 찼어도 버킷 정보는 업데이트 (다음 요청을 위해)
            redis.call('HMSET', key, 'lastProcess', lastProcess, 'queue', queue)
            redis.call('EXPIRE', key, 3600)
            
            -- 대기열이 가득 찼으면 요청 거부
            return false
            """;
        
        return RedisScript.of(script, Boolean.class);
    }
    
    /**
     * 슬라이딩 윈도우 카운터 알고리즘을 구현한 Lua 스크립트를 RedisScript 빈으로 등록합니다.
     * 
     * 슬라이딩 윈도우 카운터 알고리즘은 시간을 작은 버킷으로 나누고 각 버킷의 요청 수를 저장합니다.
     * 현재 윈도우와 이전 윈도우의 가중 합계를 사용하여 속도 제한을 계산합니다.
     * 이 방식은 슬라이딩 윈도우 로그보다 메모리 효율적이며 여전히 윈도우 경계에서 트래픽 급증을 방지합니다.
     * 
     * @return 슬라이딩 윈도우 카운터 속도 제한 로직을 수행하는 RedisScript
     */
    @Bean
    public RedisScript<Boolean> slidingWindowCounterScript() {
        // 인라인 Lua 스크립트 정의 - 텍스트 블록 사용
        String script = """
            -- 입력 파라미터 추출
            local key = KEYS[1]                -- Redis 키 (사용자 ID, IP 등)
            local now = tonumber(ARGV[1])      -- 현재 시간 (밀리초)
            local limit = tonumber(ARGV[2])    -- 허용된 최대 요청 수
            local period = tonumber(ARGV[3])   -- 시간 기간 (밀리초)
            
            -- 윈도우 크기 계산 (1분 = 60,000 밀리초)
            local window_size = 60000
            
            -- 현재 윈도우의 시작 시간 계산 (현재 시간을 윈도우 크기로 나눈 몫 * 윈도우 크기)
            local current_window = math.floor(now / window_size) * window_size
            
            -- 이전 윈도우의 시작 시간 계산 (현재 윈도우 - 윈도우 크기)
            local previous_window = current_window - window_size
            
            -- 현재 윈도우와 이전 윈도우의 키 생성
            local current_key = key .. ":" .. current_window
            local previous_key = key .. ":" .. previous_window
            
            -- 현재 윈도우와 이전 윈도우의 카운터 가져오기
            local current_count = tonumber(redis.call('GET', current_key)) or 0
            local previous_count = tonumber(redis.call('GET', previous_key)) or 0
            
            -- 현재 윈도우에서 경과된 시간의 비율 계산 (0.0 ~ 1.0)
            local current_window_elapsed = (now - current_window) / window_size
            
            -- 이전 윈도우에서 남아있는 시간의 비율 계산 (0.0 ~ 1.0)
            local previous_window_remaining = 1 - current_window_elapsed
            
            -- 슬라이딩 윈도우 내의 요청 수 계산 (가중 합계)
            -- 이전 윈도우의 카운트에 남은 시간 비율을 곱하고, 현재 윈도우의 카운트를 더함
            local weighted_count = previous_count * previous_window_remaining + current_count
            
            -- 요청 수가 제한보다 작으면 요청 추가
            if weighted_count < limit then
              -- 현재 윈도우의 카운터 증가
              redis.call('INCR', current_key)
              
              -- 키 만료 시간 설정 (현재 윈도우 + 다음 윈도우 = 2 * 윈도우 크기)
              -- 이전 윈도우는 현재 윈도우가 끝난 후에도 필요하므로 2배의 윈도우 크기로 설정
              redis.call('EXPIRE', current_key, 2 * window_size / 1000)
              
              -- 이전 윈도우의 키도 만료 시간 설정 (필요한 경우)
              if previous_count > 0 then
                redis.call('EXPIRE', previous_key, window_size / 1000)
              end
              
              -- 요청 허용
              return true
            end
            
            -- 요청 수가 제한 이상이면 요청 거부
            return false
            """;
        
        return RedisScript.of(script, Boolean.class);
    }
}