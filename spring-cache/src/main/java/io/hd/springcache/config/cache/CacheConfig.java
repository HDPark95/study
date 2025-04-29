package io.hd.springcache.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    CacheManager cacheManager(){
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

        List<CaffeineCache> caffeineCaches =  Arrays.stream(CacheType.values())
                .map(cacheType -> new CaffeineCache(
                        cacheType.getCacheName(),
                        Caffeine.newBuilder()
                                .recordStats()
                                .expireAfterWrite(cacheType.getExpiredAfterWrite(), TimeUnit.SECONDS)
                                .maximumSize(cacheType.getMaximumSize())
                                .build(
                        )
                )).toList();

        simpleCacheManager.setCaches(caffeineCaches);
        return simpleCacheManager;
    }
}
