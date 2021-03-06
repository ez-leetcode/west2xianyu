package com.west2xianyu;

import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.mapper.RefundMapper;
import com.west2xianyu.mapper.RoleMapper;
import com.west2xianyu.mapper.UserRoleMapper;
import com.west2xianyu.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class West2xianyuApplicationTests {

    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsMapper goodsMapper;

    /*

    @Test
    void fun() {
        int result = goodsMapper.deleteById(1390677138446315522L);
        System.out.println(result);
    }


    @Test
    void fun1(){
        //System.out.println(redisTemplate.getExpire("171909060",TimeUnit.SECONDS));
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
