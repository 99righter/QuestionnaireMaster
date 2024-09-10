package com.dazhuang.answerPlatform.constant;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public interface CaffeineConstant {
    /**
     * 分布式锁常亮
     */
    String AI_LOCK_KEY = "AI_CAFFEINE_LOCK_KEY";
    /**
     * 本地缓存常量
     */
    Cache<String, String> ANSWER_MAP = Caffeine.newBuilder()
            .initialCapacity(1024)// 初始容量
            .expireAfterWrite(10, TimeUnit.MINUTES)//10min后移除
            .build();
}
