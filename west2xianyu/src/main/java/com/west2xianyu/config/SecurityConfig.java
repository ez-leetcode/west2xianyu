package com.west2xianyu.config;


import com.west2xianyu.filter.LoginFilter;
import com.west2xianyu.handler.MyAccessDeniedHandler;
import com.west2xianyu.handler.MyAuthenticationFailureHandler;
import com.west2xianyu.handler.MyAuthenticationSuccessHandler;
import com.west2xianyu.service.UserDetailServiceImpl;
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private DataSource dataSource;


    @Autowired
    private UserDetailServiceImpl userDetailService;


    @Bean
    public PasswordEncoder getPassword(){
        //密码加密强度：5
        return new BCryptPasswordEncoder(5);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //不通过security
        //可能还要有图片
        web.ignoring().antMatchers("/yzm.jpg");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //在校验密码之前先校验验证码
        http.addFilterBefore(new LoginFilter(),UsernamePasswordAuthenticationFilter.class);


        http.authorizeRequests()
                .antMatchers("/yzm.jpg/**").permitAll()
                .antMatchers("/login").permitAll()
                //拥有权限
                .antMatchers("/abc").hasAuthority("admin")
                //拥有角色
                .antMatchers("/def").hasRole("admin")
                //其他所有请求必须认证才能访问，必须登录
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                .csrf().disable()
                .formLogin()
                .successHandler(new MyAuthenticationSuccessHandler())
                .failureHandler(new MyAuthenticationFailureHandler())
                .permitAll();



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
