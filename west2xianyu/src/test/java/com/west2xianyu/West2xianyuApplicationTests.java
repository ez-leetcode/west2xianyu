package com.west2xianyu;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.west2xianyu.mapper.RefundMapper;
import com.west2xianyu.mapper.RoleMapper;
import com.west2xianyu.mapper.UserRoleMapper;
import com.west2xianyu.pojo.Refund;
import com.west2xianyu.pojo.Role;
import com.west2xianyu.pojo.UserRole;
import com.west2xianyu.utils.JwtUtils;
import com.west2xianyu.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.awt.geom.QuadCurve2D;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

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

    /*
    @Test
    void fun() {
        QueryWrapper<Refund> wrapper = new QueryWrapper<>();
        wrapper.eq("number","1386951846640848877");
        Refund refund = refundMapper.selectOne(wrapper);
        if(refund != null){
            System.out.println(refund);
        }
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
