package com.west2xianyu.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@Setter
@Getter
public class RedisUtils {

    private StringRedisTemplate redisTemplate;


    //删除key
    public void delete(String key){
        redisTemplate.delete(key);
    }

    public String getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

}
