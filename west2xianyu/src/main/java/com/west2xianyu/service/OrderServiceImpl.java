package com.west2xianyu.service;

import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.mapper.OrderMapper;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.pojo.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public String generateOrder(Long number, String toId) {
        Goods goods = goodsMapper.selectById(number);
        if(goods == null){
            log.warn("该闲置物品不存在，编号：" + number);
            return "existWrong";
        }
        if(goods.getIsFrozen() == 1){
            log.warn("该闲置物品已被冻结，编号：" + number);
            return "frozenWrong";
        }
        Order order = new Order();
        //设置订单卖家和买家
        order.setFromId(goods.getFromId());
        order.setToId(toId);
        //图片url
        order.setPhoto(goods.getPhoto());
        //设置价格
        order.setPrice(goods.getPrice());
        orderMapper.insert(order);
        log.info("订单生成成功，订单：" + order.toString());
        return "success";
    }

    @Override
    public Order getOrder(Long number) {
        return orderMapper.selectById(number);
    }
}