package com.west2xianyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.west2xianyu.mapper.RoleMapper;
import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.mapper.UserRoleMapper;
import com.west2xianyu.pojo.Role;
import com.west2xianyu.pojo.User;
import com.west2xianyu.pojo.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.info("正在尝试登录");
        //先查询用户名是否存在
        log.info("正在查询用户名是否存在：" + s);
        User user = userMapper.selectById(s);
        //查询用户是否被冻结，获取用户信息，判断解封时间是否比当前时间久
        if(user == null || (user.getFrozenDate() != null && user.getFrozenDate().after(new Date()))){
            log.warn("用户名不存在或已经被冻结：" + s);
            if(user != null){
                throw new UsernameNotFoundException(user.getFrozenDate().toString());
            }else{
                throw new UsernameNotFoundException("用户不存在");
            }
        }
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user",user.getId());
        Collection<GrantedAuthority> authList = new ArrayList<>();
        List<UserRole> userRoleList = userRoleMapper.selectList(wrapper);
        for(UserRole x:userRoleList){
            //获取用户角色
            Role role = roleMapper.selectById(x.getRole());
            //把角色赋予用户
            authList.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        }
        log.info("用户拥有的权限：" + authList.toString());
        //2021.4.16 23:56
        return new org.springframework.security.core.userdetails.User(user.getId(),user.getPassword(),authList);
    }

}
