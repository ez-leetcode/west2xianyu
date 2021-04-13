package com.west2xianyu;

import com.west2xianyu.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;

@SpringBootTest
class West2xianyuApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;



    @Test
    void contextLoads() {
        ValueOperations<String,String> operations = redisTemplate.opsForValue();
        operations.set("woai","ycy");
        System.out.println(operations.get("woai"));
    }



}
