package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Orders;

public interface OrderService {

    String generateOrder(Long number,String toId);

    Orders getOrder(Long number);

    String deleteOrder(Long number,String id,int flag);

    String evaluateOrder(Long number,String fromId,String toId,double describe,double service,double logistics,int isNoname,String evaluation);

    JSONObject getOrderList(String id,String keyword,int status,long cnt,long page);

    String confirmOrder(Long number,String fromId,String toId);
}
