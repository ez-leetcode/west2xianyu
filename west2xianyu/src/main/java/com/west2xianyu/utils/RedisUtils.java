package com.west2xianyu.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Setter
@Getter
@Component
public class RedisUtils {


    @Autowired
    private StringRedisTemplate redisTemplate;

    //存key-value
    public void save(String key,String value){
        redisTemplate.opsForValue().set(key,value);
    }

    //存带有过期时间的key-value
    public void saveByTime(String key,String value,int hours){
        redisTemplate.opsForValue().set(key,value,hours,TimeUnit.MINUTES);
    }

    public void saveByMinutesTime(String key,String value,int minutes){
        redisTemplate.opsForValue().set(key,value,minutes,TimeUnit.MINUTES);
    }

    //删除key
    public void delete(String key){
        redisTemplate.delete(key);
    }

    public String getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }





    /*
    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

     */

}
