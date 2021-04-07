package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.EvaluateMapper;
import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.mapper.OrdersMapper;
import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.msg.OrderMsg;
import com.west2xianyu.pojo.Evaluate;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.pojo.Orders;
import com.west2xianyu.pojo.User;
import jdk.dynalink.linker.LinkerServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private EvaluateMapper evaluateMapper;

    @Autowired
    private UserMapper userMapper;


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

    @Override
    public String evaluateOrder(Long number, String fromId, String toId, double describe,
                                double service, double logistics, int isNoname, String evaluation) {
        //1.判断是否可以评价  2.改变商品状态  3.保存评价  4.更新商家3项分数及销售量
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("评论失败，该订单不存在或已被冻结：" + number);
            return "existWrong";
        }
        if(orders.getStatus() != 5){
            log.warn("评论失败，该订单尚未完成：" + orders.getStatus());
            return "statusWrong";
        }
        User user = userMapper.selectById(fromId);
        if(user == null){
            log.warn("评论失败，卖家不存在：" + fromId);
            return "userWrong";
        }
        Date date = new Date();
        orders.setStatus(6);
        orders.setFinishTime(date);
        ordersMapper.updateById(orders);
        log.info("改变商品状态成功");
        evaluateMapper.updateById(new Evaluate(number,fromId,toId,evaluation,describe,service,logistics,isNoname,null));
        log.info("保存评价成功");
        int cnt = user.getSaleCounts();
        double describe1 = user.getAveDescribe() * cnt + describe;
        double service1 = user.getAveService() * cnt + service;
        double logistics1 = user.getAveLogistics() * cnt + logistics;
        cnt ++;
        describe1 /= cnt;
        service1 /= cnt;
        logistics1 /= cnt;
        user.setSaleCounts(cnt);
        user.setAveDescribe(describe1);
        user.setAveService(service1);
        user.setAveLogistics(logistics1);
        userMapper.updateById(user);
        log.info("更新卖家信息成功：" + user.toString());
        //通知卖家
        log.info("评价成功");
        return "success";
    }


    @Override
    public JSONObject getOrderList(String id, String keyword, int status, long cnt, long page) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        Page<Orders> page1 = new Page<>(page,cnt);
        //添加查询条件
        wrapper.eq("to_id",id)
                .orderByDesc("order_time");
        if(keyword != null){
            wrapper.like("number",keyword)
                    .like("goods_name",keyword);
        }
        //全部查询status = -1
        if(status != -1){
            wrapper.eq("status",status);
        }
        ordersMapper.selectPage(page1,wrapper);
        List<Orders> ordersList = page1.getRecords();
        List<OrderMsg> orderMsgList = new LinkedList<>();
        for(Orders x:ordersList){
            User user = userMapper.selectById(x.getFromId());
            orderMsgList.add(new OrderMsg(x.getNumber(),x.getFromId(),user.getUsername(),
                    x.getGoodsName(),x.getPrice(),x.getFreight(),x.getPhoto(),x.getOrderTime()));
        }
        log.info("获取订单列表成功：" + orderMsgList.toString());
        jsonObject.put("orderList",orderMsgList);
        jsonObject.put("pages",page1.getPages());
        return jsonObject;
    }
}