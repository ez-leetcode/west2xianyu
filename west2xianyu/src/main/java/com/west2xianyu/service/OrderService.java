package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.msg.OrderMsg;
import com.west2xianyu.pojo.Orders;
import org.springframework.web.multipart.MultipartFile;

public interface OrderService {

    String generateOrder(Long number,String toId,String message,long address);

    OrderMsg getOrder(Long number);

    String deleteOrder(Long number,String id,int flag);

    String evaluateOrder(Long number,String fromId,String toId,double describe,double service,double logistics,int isNoname,String evaluation,String photo);

    JSONObject getOrderList(String id,String keyword,int status,long cnt,long page);

    String confirmOrder(Long number,String fromId,String toId);

    String cancelOrder(Long number,String id);

    String saveRefund(Long number,String id,double money,String reason,String photo,String description);

    String judgeRefund1(Long number,String fromId,Integer isPass,String reason);

    String sendOrder(Long number,String fromId);

    String refundPhotoUpload(MultipartFile file);

    String evaluatePhotoUpload(MultipartFile file);

    Orders getOrders(Long number);
}
