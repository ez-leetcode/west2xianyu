package com.west2xianyu.service;

import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.mapper.OrdersMapper;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.pojo.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrdersMapper ordersMapper;

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
        Orders orders = new Orders();
        //设置订单卖家和买家
        orders.setFromId(goods.getFromId());
        orders.setToId(toId);
        //图片url
        orders.setPhoto(goods.getPhoto());
        //设置价格
        orders.setPrice(goods.getPrice());
        ordersMapper.insert(orders);
        log.info("订单生成成功，订单：" + orders.toString());
        return "success";
    }

    @Override
    public Orders getOrder(Long number) {
        return ordersMapper.selectById(number);
    }

    @Override
    public String deleteOrder(Long number,String id,int flag) {
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("删除订单失败");
            return "existWrong";
        }
        ordersMapper.deleteById(number);
        log.info("删除订单信息成功：" + orders.toString());
        return "success";
    }
}