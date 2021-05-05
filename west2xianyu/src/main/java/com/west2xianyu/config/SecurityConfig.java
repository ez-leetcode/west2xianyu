package com.west2xianyu.config;


import com.west2xianyu.filter.MyUsernamePasswordFilter;
import com.west2xianyu.handler.MyAccessDeniedHandler;
import com.west2xianyu.handler.MyAuthenticationFailureHandler;
import com.west2xianyu.handler.MyAuthenticationSuccessHandler;
import com.west2xianyu.handler.MyLogoutSuccessHandler;
import com.west2xianyu.service.UserDetailServiceImpl;
import com.west2xianyu.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private RedisUtils redisUtils;


    @Bean
    public PasswordEncoder getPassword(){
        //密码加密强度：5
        return new BCryptPasswordEncoder(5);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(getPassword());
    }

    //放行静态资源
    @Override
    public void configure(WebSecurity web) throws Exception {
        //不通过security
        //可能还要有图片
        //swagger放行这四个，不然看不见
        web.ignoring().antMatchers("/swagger-ui.html")
                .antMatchers("/webjars/**")
                .antMatchers("/v2/**")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/yzm.jpg")
                .antMatchers("/payBill/**")
                .antMatchers("/notifyBill/**")
                .antMatchers("/refundBill/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(new MyUsernamePasswordFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                //测试的时候放行全部
                .antMatchers("/**").permitAll()
                //放行swagger
                .antMatchers("/swagger-ui.html").permitAll()
                //放行注册接口
                .antMatchers("/register").permitAll()
                //放行验证码获取接口
                .antMatchers("/yzm.jpg/**").permitAll()
                //放行登录接口
                .antMatchers("/login").permitAll()
                //放行支付宝订单生成接口
                .antMatchers("/payBill").permitAll()
                //放行支付宝异步反馈接口
                .antMatchers("/notifyBill").permitAll()
                //放行支付宝退款接口
                .antMatchers("/refundBill").permitAll()
                //拥有管理员角色
                .antMatchers("/admin/**").hasRole("ADMIN")
                //拥有用户角色
                //.antMatchers("/user/**").hasRole("user")
                //其他所有请求必须认证才能访问，必须登录
                .anyRequest().authenticated()
                .and()
                //解决跨域
                .cors()
                .and()
                //因为用token认证，所以关闭csrf
                .csrf().disable()
                .formLogin()
                //登录url
                .loginProcessingUrl("/login")
                //用户名参数
                .usernameParameter("id")
                //密码参数
                .passwordParameter("password")
                //登录成功处理
                .successHandler(myAuthenticationSuccessHandler)
                //登录失败处理
                .failureHandler(myAuthenticationFailureHandler)
                .permitAll()
                .and()
                .logout()
                //注销url
                .logoutUrl("/logout")
                //成功退出登录处理
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .and()
                .exceptionHandling()
                //登录后访问没有权限处理
                .accessDeniedHandler(myAccessDeniedHandler);
        //异常处理
        // http.exceptionHandling()
        //        .accessDeniedHandler(myAccessDeniedHandler);

        //跨域
       // http.cors();

        //由于有了token认证机制，所以关闭csrf防护
        //http.csrf().disable();
    }

    //token持久化配置
    /*
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }
    */


}
