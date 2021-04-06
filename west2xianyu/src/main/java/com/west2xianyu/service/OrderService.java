package com.west2xianyu.service;

import com.west2xianyu.pojo.Orders;

public interface OrderService {

    String generateOrder(Long number,String toId);

    Orders getOrder(Long number);

    String deleteOrder(Long number,String id,int flag);
}
