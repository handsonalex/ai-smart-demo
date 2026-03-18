package com.smart.common.constants;

/**
 * 缓存过期时间常量（单位：秒）
 *
 * @author Joseph Ho
 */
public final class CacheExpire {

    private CacheExpire() {
    }

    public static final long ONE_MINUTE = 60L;

    public static final long FIVE_MINUTES = 300L;

    public static final long TEN_MINUTES = 600L;

    public static final long ONE_HOUR = 3600L;

    public static final long ONE_DAY = 86400L;
}
