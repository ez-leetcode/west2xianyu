package com.west2xianyu.filter;


import com.west2xianyu.utils.JwtUtils;
import com.west2xianyu.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.Collection;


@Slf4j
public class MyUsernamePasswordFilter extends OncePerRequestFilter {


    //重写拦截器方法
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //尝试获取token，没有直接放行（因为没有token会没有身份不让用接口，放行无所谓）
        String token = request.getHeader("token");
        if(token == null){
            //没有token,直接放行，给个游客身份（不能不给身份，会跳到默认的登录界面的）
            Collection<GrantedAuthority> authList = new ArrayList<>();
            authList.add(new SimpleGrantedAuthority("ROLE_YK"));
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new User("1","1",authList),null,authList));
            chain.doFilter(request,response);
            return ;
        }
        log.info("正在进行身份验证");
        //有token则获取身份信息
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        if(applicationContext != null){
            RedisUtils redisUtils = (RedisUtils) applicationContext.getBean("redisUtils");
            //先查询redis数据库中是否有这个token
            //先获取token中的用户名
            String username = JwtUtils.getUsername(token);
            //再获取redis中对应的token
            String redisToken = redisUtils.getValue(username);
            log.info("username：" + username);
            log.info("token：" + token);
            log.info("redisToken：" + redisToken);
            if(username != null && token.equals(redisToken)){
                //身份验证token正确，赋予角色
                log.info("正在赋予身份信息");
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = redisUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }else{
                log.info("身份信息校验错误");
                throw new UserPrincipalNotFoundException("用户身份信息错误");
            }
        }
        log.info("身份信息：" + SecurityContextHolder.getContext().toString());
        chain.doFilter(request,response);
    }

}