package io.hd.springcache.config.cache;

import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    GOODS("goods", ConstantsConfig.EXPIRED_AFTER_WRITE, ConstantsConfig.MAXIMUM_SIZE);

    private final String cacheName;
    private final int expiredAfterWrite; //
    private final int maximumSize;

    class ConstantsConfig{
        public static final int EXPIRED_AFTER_WRITE = 60;
        public static final int MAXIMUM_SIZE = 1000;
    }
}
