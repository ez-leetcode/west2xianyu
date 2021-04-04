package com.west2xianyu.service;

import com.west2xianyu.pojo.Order;

public interface OrderService {

    String generateOrder(Long number,String toId);

    Order getOrder(Long number);

    String deleteOrder(Long number,String id,int flag);
}
