package com.west2xianyu.service;

import com.west2xianyu.pojo.User;

public interface UserService {

    String register(User user);

    String login(User user);

    User getUser(String Id);

    void saveUser(User user);
}
