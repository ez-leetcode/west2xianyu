package com.west2xianyu.config;


import com.west2xianyu.filter.MyLoginFilter;
import com.west2xianyu.handler.MyAccessDeniedHandler;
import com.west2xianyu.handler.MyAuthenticationFailureHandler;
import com.west2xianyu.handler.MyAuthenticationSuccessHandler;
import com.west2xianyu.handler.MyLogoutSuccessHandler;
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


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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


    @Bean
    public PasswordEncoder getPassword(){
        //密码加密强度：5
        return new BCryptPasswordEncoder(5);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(getPassword());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //不通过security
        //可能还要有图片
        web.ignoring().antMatchers("/yzm.jpg")
                .antMatchers("/pay");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //在校验密码之前先校验验证码
        //http.addFilterBefore(new MyLoginFilter(),UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .antMatchers("/yzm.jpg/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/pay").permitAll()
                //拥有角色
                .antMatchers("/admin/**").hasRole("admin")
                //其他所有请求必须认证才能访问，必须登录
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                //因为用token认证，所以关闭csrf
                .csrf().disable()
                .formLogin()
                //登录url
                .loginProcessingUrl("/login")
                //登录成功处理
                .successHandler(myAuthenticationSuccessHandler)
                //登录失败处理
                .failureHandler(myAuthenticationFailureHandler)
                .permitAll()
                .and()
                .logout()
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
