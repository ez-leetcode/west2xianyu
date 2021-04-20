package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Address;
import com.west2xianyu.pojo.User;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    String register(User user);

    String login(User user);

    User getUser(String Id);

    int saveUser(User user);

    String addShopping(Long number,String Id);

    String deleteShopping(Long number,String Id);

    String addFans(String id,String fansId);

    String addComment(Long goodsId,String id,String comments);

    String deleteComment(Long goodsId, String id, String comments, String createTime);

    String deleteFans(String id,String fansId);

    String addFeedback(String id,String phone,String feedbacks,String title);

    String addLikes(Long goodsId,String id,String comments,String createTime);

    String deleteLikes(Long goodsId,String id,String comments,String createTime);

    String uploadPhoto(MultipartFile file,String id);

    String addAddress(String id,String campus,String realAddress,String name,String phone,int isDefault);

    String deleteAddress(Long number,String id);

    String changeAddress(Address address);

    JSONObject getShopping(String id, long cnt, long page);

    JSONObject getHistory(String id,long cnt,long page);

    JSONObject getFollow(String id,long cnt,long page);

    JSONObject getAddress(String id,long cnt,long page);

    JSONObject getMessage(String id,long cnt,long page,int isRead);

    JSONObject getOneMessage(String id,Long number);

    String deleteHistory(Long goodsId,String id);

    String deleteAllHistory(String id);

    String readAllMessage(String id);

    String deleteAllShopping(String id);

    User getUserWhenever(String id);

    String changePassword(String id,String oldPassword,String newPassword);

    String findPassword(String id,String newPassword);
}
