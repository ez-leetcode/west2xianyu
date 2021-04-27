package com.west2xianyu.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
@Component
@Slf4j
public class RedisUtils {


    @Autowired
    private StringRedisTemplate redisTemplate;

    //存key-value
    public void save(String key,String value){
        redisTemplate.opsForValue().set(key,value);
    }

    //存带有过期时间的key-value
    public void saveByTime(String key,String value,int hours){
        //为防止缓存雪崩  加一个随机时间
        Random random = new Random();
        long minute = hours * 60L + random.nextInt(10);
        redisTemplate.opsForValue().set(key,value,minute,TimeUnit.MINUTES);
    }

    public void saveByMinutesTime(String key,String value,int minutes){
        //同理
        Random random = new Random();
        long seconds = minutes * 60L + random.nextInt(10);
        redisTemplate.opsForValue().set(key,value,seconds,TimeUnit.SECONDS);
    }

    //保存浏览量信息
    public void saveScan(String key){
        //加前缀方便识别
        String value = redisTemplate.opsForValue().get("scan_" + key);
        log.info(key);
        log.info(value);
        if(value == null){
            //不存在key 则存一个，浏览量1
            redisTemplate.opsForValue().set("scan_" + key,"1");
        }else{
            //key存在，获取当前浏览量
            int cnt = Integer.parseInt(value);
            cnt ++;
            //保存更新的浏览量
            redisTemplate.opsForValue().set("scan_" + key, Integer.toString(cnt));
        }
    }

    //获取所有浏览信息
    public Map<String,Integer> getAllScan(){
        Map<String,Integer> map = new HashMap<>();
        Set<String> keySet = redisTemplate.keys("scan_*");
        if(keySet != null){
            //有需要更新的浏览信息
            log.info(keySet.toString());
            for(String x:keySet){
                //获取value
                String value = redisTemplate.opsForValue().get(x);
                if(value != null){
                    //把key处理一下，和商品页面的编号格式一样
                    map.put(x.substring(x.lastIndexOf("_") + 1),Integer.parseInt(value));
                }
            }
        }
        log.info("map:" + map.toString());
        return map;
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
