package com.west2xianyu.service;


import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    //有时间加邮箱验证码
    @Override
    public String register(User user) {
        User user1 = userMapper.selectById(user.getId());
        if(user1 != null){
            log.warn("注册失败，该用户已被注册：" + user.getId());
            return "repeatWrong";
        }

        //之前加一个验证码是否正确的验证
        log.info("正在创建新帐号");
        //设置默认邮箱
        user.setEmail(user.getId() + "@fzu.edu.cn");
        //默认用户名是学号
        user.setUsername(user.getId().toString());
        userMapper.insert(user);
        log.info("新账号：" + user.toString());
        return "success";
    }

    @Override
    public String login(User user) {
        User user1 = userMapper.selectById(user.getId());
        if(user1 == null){
            log.warn("登录失败，用户不存在：" + user.getId());
            return "existWrong";
        }
        return "success";
    }

    @Override
    public User getUser(String Id) {
        return userMapper.selectById(Id);
    }

    public void saveUser(User user){
        //4.1
        ;
    }
}
