package com.example.demo.redis.model;

import java.io.Serializable;

public class RedisKeyModel implements Serializable {

    /**
     * tair使用的KEY
     */
    private String key;
    /**
     * 过期时间，单位：秒（永久=0）
     */
    private Integer expire;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }
}
