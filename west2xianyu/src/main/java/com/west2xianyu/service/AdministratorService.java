package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;

public interface AdministratorService {

    JSONObject getAllFeedback(String id,Long cnt,Long page,int isHide);

    JSONObject getFeedback(String id,Long number);


}
