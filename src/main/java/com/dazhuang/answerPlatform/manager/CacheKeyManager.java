package com.dazhuang.answerPlatform.manager;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class CacheKeyManager {
    /**
     * 构建缓存的key,构建的名字为appId+md5(用户选项),中间通过:分割，通过:分割便于实现当更改app题目的时候，删除以前的缓存
     */
    public String buildCacheKey(Long appId, String choiceStr) {
        return appId + ":" + DigestUtils.md5Hex(choiceStr);
    }
}
