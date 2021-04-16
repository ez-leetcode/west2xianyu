package com.west2xianyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.mapper.UserRoleMapper;
import com.west2xianyu.pojo.User;
import com.west2xianyu.pojo.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


//UserDetailsService实例
@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //先查询用户名是否存在
        log.info("正在查询用户名是否存在：" + s);
        User user = userMapper.selectById(s);
        if(user == null){
            log.warn("用户名不存在：" + s);
            throw new UsernameNotFoundException("用户名不存在：" + s);
        }
        //用户名存在的情况下，比较密码，待完成
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user",user.getId());
        List<UserRole> userRoleList = userRoleMapper.selectList(wrapper);
        //2021.4.16 23:56
        //密码呢？？ 等等再说
        return null;
    }

}
