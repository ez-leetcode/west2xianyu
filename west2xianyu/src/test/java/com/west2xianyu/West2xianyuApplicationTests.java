package com.west2xianyu;

import com.west2xianyu.utils.JwtUtils;
import com.west2xianyu.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;

@SpringBootTest
class West2xianyuApplicationTests {

    @Autowired
    private RedisUtils redisUtils;


    @Test
    void fun(){
        String yzm = null;
        if(yzm == null || yzm.equals("12")){
            System.out.println(11111111);
        }
       // redisUtils.delete("ycy");
       // System.out.println(1);
        //redisUtils.saveByTime("ycy","yyds",1);
    }


   /*


    @Autowired
    private RedisUtils redisUtils;

    @Test
    void contextLoads() {
        ValueOperations<String,String> operations = redisTemplate.opsForValue();
        operations.set("woai","ycy");
        System.out.println(operations.get("woai"));
    }


    @Test
    void fun1(){
        String realUsername = JwtUtils.getUsername("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IjEyMyIsImV4cCI6MTYxNTQzMTQ4NiwidXNlcm5hbWUiOiJnYW94dSJ9.iiA1lBZ3MiFuc8dendikYtYHoMtDfx4CB7cAtS7sxJc");
        System.out.println(realUsername);
    }



     */



}
