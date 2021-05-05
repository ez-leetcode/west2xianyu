package com.west2xianyu.filter;

import com.west2xianyu.utils.JwtUtils;
import com.west2xianyu.utils.RedisUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Setter
//这个暂时弃用
public class MyLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //post请求并且是login的路径，测试的时候先不用
        /*
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/login".equals(request.getServletPath())) {
            //用户输入的验证码
            String code = request.getParameter("code");
            //真正的验证码
            String realCode = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            log.info("用户输入的验证码：" + code + " 真正的验证码：" + realCode);
            //验证码默认不能为空
            if (!realCode.toLowerCase(Locale.ROOT).equalsIgnoreCase(code.toLowerCase(Locale.ROOT))) {
                //验证码不正确
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter printWriter = response.getWriter();
                printWriter.write(ResultUtils.getResult(new JSONObject(), "yzmWrong").toString());
                printWriter.flush();
                printWriter.close();
            }
        }
         */
        //非登录状态请求，检查token
        if (!"/login".equals(request.getServletPath())) {
            response.setContentType("application/json;charset=UTF-8");
            //不是登录状态，检查token是否合格
            String token = request.getHeader("token");
            if (token == null) {
                log.warn("token不存在");
                //printWriter.write(ResultUtils.getResult(new JSONObject(), "tokenWrong").toString());
                //printWriter.flush();
            } else {
                String username = request.getParameter("id");
                //因为拦截器之前未实例化bean，所以只能自己创建一个
                //StringRedisTemplate redisTemplate = new StringRedisTemplate();
                WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
                if(applicationContext != null){
                    RedisUtils redisUtils = (RedisUtils) applicationContext.getBean("redisUtils");
                    //String realToken = redisTemplate.opsForValue().get(username);
                    String realToken = redisUtils.getValue(username);
                    String realUsername = JwtUtils.getUsername(token);
                    log.info("token：" + token + " realToken：" + realToken);
                    if (realToken == null || !username.equals(realUsername) || !JwtUtils.isExpiration(token) || !realToken.equals(token)) {
                        //token过期  token不存在  token不匹配  用户名不匹配都认证失败
                        log.warn("token错误");
                        //printWriter.write(ResultUtils.getResult(new JSONObject(), "tokenWrong").toString());
                        //printWriter.flush();
                    }else{
                        //token认证成功
                        log.info("123");
                    }
                }
            }
        }
        //printWriter.close();
        chain.doFilter(request, response);
    }
}
