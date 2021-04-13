package com.west2xianyu.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.Constants;
import com.west2xianyu.utils.JwtUtils;
import com.west2xianyu.utils.RedisUtils;
import com.west2xianyu.utils.ResultUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //post请求并且是login的路径
        if("POST".equalsIgnoreCase(httpServletRequest.getMethod()) && "/login".equals(httpServletRequest.getServletPath())){
            //用户输入的验证码
            String code = httpServletRequest.getParameter("code");
            //真正的验证码
            String realCode = (String) httpServletRequest.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            log.info("用户输入的验证码：" + code + " 真正的验证码：" + realCode);
            //验证码默认不能为空
            if(!realCode.toLowerCase(Locale.ROOT).equalsIgnoreCase(code.toLowerCase(Locale.ROOT))){
                //验证码不正确
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                PrintWriter printWriter = httpServletResponse.getWriter();
                printWriter.write(ResultUtils.getResult(new JSONObject(),"yzmWrong").toString());
                printWriter.flush();
                printWriter.close();
            }
        }
        //非登录状态请求，检查token
        if(!"/login".equals(httpServletRequest.getServletPath())){
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter printWriter = httpServletResponse.getWriter();
            //不是登录状态，检查token是否合格
            String token = httpServletRequest.getHeader("token");
            if(token == null){
                log.warn("token不存在");
                printWriter.write(ResultUtils.getResult(new JSONObject(),"yzmWrong").toString());
                printWriter.flush();
                printWriter.close();
            }else{
                String username = httpServletRequest.getParameter("id");
                String realToken = redisUtils.getValue(username);
                String realUsername = JwtUtils.getUsername(token);
                log.info("token：" + token + " realToken：" + realToken);
                if(realToken == null || !username.equals(realUsername) || !JwtUtils.isExpiration(token) || !realToken.equals(token)){
                    //token过期  token不存在  token不匹配  用户名不匹配都认证失败
                    log.warn("token错误");
                    printWriter.write(ResultUtils.getResult(new JSONObject(),"yzmWrong").toString());
                    printWriter.flush();
                    printWriter.close();
                }
            }
        }
        chain.doFilter(httpServletRequest,httpServletResponse);
    }
}
