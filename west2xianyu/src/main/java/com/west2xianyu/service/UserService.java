package com.west2xianyu.service;

import com.west2xianyu.pojo.User;

public interface UserService {

    String register(User user);

    String login(User user);

    User getUser(String Id);

    int saveUser(User user);

    String addShopping(Long number,String Id);

    String deleteShopping(Long number,String Id);

    String addFans(String id,String fansId);

    String addComment(Long goodsId,String id,String comments);
}
