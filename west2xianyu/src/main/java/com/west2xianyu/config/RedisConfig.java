package com.west2xianyu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    //redis序列化配置，数据库显示起码不要乱码吧~
    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("redis正在序列化");
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        //key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //hash类型  key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //hash类型  value序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        //注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        log.info("redis序列化成功");
        return redisTemplate;
    }

}
