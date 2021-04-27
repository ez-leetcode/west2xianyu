package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.User;

public interface AdministratorService {

    JSONObject getAllFeedback(String id,Long cnt,Long page,int isHide);

    JSONObject getFeedback(String id,Long number);

    JSONObject getAllUser(String keyword,Long cnt,Long page);

    JSONObject getGoodsList(String keyword,Long cnt,Long page,Integer isPass);

    JSONObject getAllUser1(int isDeleted,String keyword,Long cnt,Long page);

    User frozeUser(String id, String reason, int days);

    String reopenUser(String id,String adminId);

    String judgeGoods(Long number,String id,int isPass);

    String judgeRefund(Long number,String id,int isPass);

    JSONObject getRefund(Long number);
}
