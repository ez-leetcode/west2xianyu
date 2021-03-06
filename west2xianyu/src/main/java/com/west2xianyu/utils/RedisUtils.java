package com.west2xianyu.utils;


import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.pojo.User;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Autowired
    private UserMapper userMapper;


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


    //判断key是否在这个时间后
    public boolean isAfterDate(String key,int minutes){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS) > (long) minutes * 60;
    }


    //token重置时间
    public void resetExpire(String key,String value,int hours){
        redisTemplate.opsForValue().set(key,value,hours,TimeUnit.HOURS);
    }


    //保存黑名单token，还可以增加次数，查ip封号
    public void saveBlackToken(String key){
        String cntString = redisTemplate.opsForValue().get(key);
        if(cntString != null){
            //获取次数并加一
            int cnt = Integer.parseInt(cntString);
            cnt ++;
            //保存犯罪次数
            redisTemplate.opsForValue().set("BLACK_" + key, Integer.toString(cnt));
        }else{
            //以前没有过，存入
            redisTemplate.opsForValue().set("BLACK_" + key,"1");
        }
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


    //从token中获取身份信息
    public UsernamePasswordAuthenticationToken getAuthentication(String token){
        //先解析token
        Claims claims = JwtUtils.getTokenBody(token);
        //获取用户名
        String username = claims.getId();
        //根据用户名判断是否为管理员，后面可根据
        User user = userMapper.selectUser(username);
        Collection<GrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(user == null){
            log.error("用户不存在");
            return null;
        }
        if(user.getIsAdministrator() == 0){
            //用户不是管理员
            log.info("身份认证成功，用户：" + username + "是普通用户");
            return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(username,user.getPassword(),authList),token,authList);
        }else{
            authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        log.info("身份认证成功，用户：" + username + "是管理员");
        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(username,user.getPassword(),authList),token,authList);
    }


    //用户判断token是否存在
    public boolean hasKey(String key){
        return redisTemplate.opsForValue().get(key) != null;
    }

}
