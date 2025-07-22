package io.hd.springcache.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        try {
            CachingProvider cachingProvider = Caching.getCachingProvider();

            ClassPathResource configResource = new ClassPathResource("ehcache.xml");
            URI uri = configResource.getURI();
            
            javax.cache.CacheManager jCacheManager = cachingProvider.getCacheManager(
                    uri, 
                    getClass().getClassLoader());

            return new JCacheCacheManager(jCacheManager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EhCache manager", e);
        }
    }
}
