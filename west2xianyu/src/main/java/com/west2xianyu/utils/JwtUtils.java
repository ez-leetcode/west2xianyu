package com.west2xianyu.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtUtils {


    private static final String secret = "*&ycy!yyds?";

    private static final long expiration = 1000 * 3600L;


    //创建token
    public static String createToken(String username, String password){
        JwtBuilder jwtBuilder = Jwts.builder()
                //设置唯一id
                .setId(username)
                .setSubject(password)
                //签发时间
                .setIssuedAt(new Date())
                //一小时过期
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256,secret);
        log.info("创建token成功，用户：" + username + " token：" + jwtBuilder.compact());
        return jwtBuilder.compact();
    }


    //解析token
    public static Claims getTokenBody(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }


    //获取用户名
    public static String getUsername(String token){
        return getTokenBody(token).getId();
    }

    //判断是否已经过期
    public static boolean isExpiration(String token){
        return getTokenBody(token).getExpiration().before(new Date());
    }


    //解析token
    public static void parseToken(String token){
        Claims claims = (Claims) Jwts.parser()
                .setSigningKey(secret)
                .parse(token)
                .getBody();
        log.info("解析token，成功：" + claims);
    }

}
