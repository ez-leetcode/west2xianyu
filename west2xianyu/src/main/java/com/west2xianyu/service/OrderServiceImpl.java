package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.*;
import com.west2xianyu.msg.OrderMsg;
import com.west2xianyu.pojo.*;
import com.west2xianyu.utils.OssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private MessageMapper messageMapper;


    @Override
    public String generateOrder(Long number, String toId) {
        Goods goods = goodsMapper.selectById(number);
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_number",number);
        List<Orders> ordersList = ordersMapper.selectList(wrapper);
        if(ordersList != null){
            log.warn("闲置物品已被下单，请重试：" + number);
            return "repeatWrong";
        }
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
        orders.setGoodsNumber(number);
        orders.setGoodsName(goods.getGoodsName());
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

    //评价订单5-6
    @Override
    public String evaluateOrder(Long number, String fromId, String toId, double describe,
                                double service, double logistics, int isNoname, String evaluation) {
        //1.判断是否可以评价  2.改变商品状态  3.保存评价  4.更新商家3项分数及销售量
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("评论失败，该订单不存在或已被冻结：" + number);
            return "existWrong";
        }
        if(orders.getStatus() != 4){
            log.warn("评论失败，该订单尚未完成：" + orders.getStatus());
            return "statusWrong";
        }
        User user = userMapper.selectById(fromId);
        if(user == null){
            log.warn("评论失败，卖家不存在：" + fromId);
            return "userWrong";
        }
        Date date = new Date();
        orders.setStatus(5);
        orders.setFinishTime(date);
        ordersMapper.updateById(orders);
        log.info("改变商品状态成功");
        Evaluate evaluate = new Evaluate(number,fromId,toId,evaluation,describe,service,logistics,isNoname,null);
        evaluateMapper.insert(evaluate);
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
        Message message = new Message();
        //注意是卖家
        message.setId(orders.getFromId());
        //未读
        message.setIsRead(0);
        message.setTitle("您的订单：" + number + " 已被评价，请及时确认");
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        message.setMsg(dateFormat.format(calendar.getTime()) + "：\n" + "  您的订单" + orders.getNumber() + "已被用户评价：" + " 描述：" + describe +
                " 服务：" + service + " 物流：" + logistics + " \n" + "  具体评价：" + evaluation);
        messageMapper.insert(message);
        log.info("评价成功");
        return "success";
    }


    @Override
    public JSONObject getOrderList(String id, String keyword, int status, long cnt, long page) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        Page<Orders> page1 = new Page<>(page,cnt);
        //添加查询条件，用户是买家或者卖家都能查到
        wrapper.eq("to_id",id)
                .or()
                .eq("from_id",id)
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
            //bug:用户被封了
            User user = userMapper.selectUser(x.getFromId());
            orderMsgList.add(new OrderMsg(x.getNumber(),x.getFromId(),user.getUsername(),
                    x.getGoodsName(),x.getPrice(),x.getFreight(),x.getPhoto(),x.getOrderTime()));
        }
        log.info("获取订单列表成功：" + orderMsgList.toString());
        jsonObject.put("orderList",orderMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        return jsonObject;
    }

    //取消订单1-0
    @Override
    public String cancelOrder(Long number, String id) {
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("取消订单失败，商品不存在或已被冻结：" + number);
            return "existWrong";
        }
        //商品存在，检查状态是否相同
        if(orders.getStatus() != 1){
            log.warn("取消订单失败，订单状态不符合要求：" + orders.getStatus());
            return "orderWrong";
        }
        //状态也符合要求，更改状态，商品解冻，通知卖家
        orders.setStatus(0);
        //更新订单状态
        ordersMapper.updateById(orders);
        Goods goods = new Goods();
        goods.setNumber(orders.getNumber());
        goods.setDeleted(0);
        //解冻商品，可能不行
        goodsMapper.updateById(goods);
        log.info("取消订单成功，订单：" + number);
        //通知卖家
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        Message message = new Message();
        message.setId(orders.getFromId());
        message.setIsRead(0);
        message.setTitle("您的订单" + number + "已被买家取消订单，请及时处理");
        message.setMsg(dateFormat.format(calendar.getTime()) + "：\n" + "您的订单已被买家" + id + "取消订单，请联系买家查询原因");
        messageMapper.insert(message);
        return "success";
    }

    //确认收货3-4
    @Override
    public String confirmOrder(Long number, String fromId, String toId) {
        //1.先看看订单状态是否相符 2.修改订单状态，通知卖家
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("订单不存在：" + number);
            return "existWrong";
        }
        if(orders.getStatus() != 3){
            log.warn("订单状态错误：" + orders.getStatus());
            return "orderWrong";
        }
        //设置订单状态
        orders.setStatus(4);
        //更新状态
        ordersMapper.updateById(orders);
        //通知卖家待完成
        Message message = new Message();
        //卖家
        message.setId(fromId);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        message.setTitle("您的订单" + number + "已被确认收货，请及时确认");
        message.setMsg(dateFormat.format(calendar.getTime()) + "： \n" + "您的订单已被买家" + toId + "确认收货");
        message.setIsRead(0);
        messageMapper.insert(message);
        log.info("通知卖家确认收货成功");
        return "success";
    }


    //订单申请退款2/3-8
    @Override
    public String saveRefund(Long number, String id, double money, String reason, String photo, String description) {
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("申请退款失败，订单不存在：" + number);
            return "existWrong";
        }
        //状态不为已付款或者已发货
        if(orders.getStatus() != 2 && orders.getStatus() != 3){
            log.warn("申请退款失败，订单不符合要求：" + orders.getStatus());
            return "statusWrong";
        }
        //消息推送待完成
        refundMapper.insert(new Refund(number,id,money,reason,description,photo,null,null));
        log.info("退款请求申请成功");
        //修改订单状态
        orders.setStatus(10);
        //更新
        ordersMapper.updateById(orders);
        log.info("订单状态更新成功");
        //通知卖家
        Message message = new Message();
        message.setId(orders.getFromId());
        message.setTitle("您的订单" + number + "被申请退款，请及时处理");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        message.setMsg(dateFormat.format(calendar.getTime()) + "： \n" + "您的订单" + number + "被用户" + id + "申请退款" + money + "元 \n" +
                " 具体原因：" + description);
        message.setIsRead(0);
        messageMapper.insert(message);
        log.info("通知卖家申请退款成功");
        return "success";
    }

    //订单确认发货2-3
    @Override
    public String sendOrder(Long number, String fromId) {
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("确认发货失败，订单不存在或已被冻结：" + number);
            return "existWrong";
        }
        if(orders.getStatus() != 2) {
            log.warn("确认发货失败，订单状态不符合要求：" + orders.getStatus());
            return "statusWrong";
        }
        if(!orders.getFromId().equals(fromId)){
            log.warn("确认发货失败，卖家错误：" + fromId);
            return "userWrong";
        }
        //修改订单状态
        orders.setStatus(3);
        //更新
        ordersMapper.updateById(orders);
        //通知买家待完成
        Message message = new Message();
        message.setIsRead(0);
        message.setId(orders.getToId());
        message.setTitle("您的订单：" + number + "卖家已发货，请及时关注");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        message.setMsg(dateFormat.format(calendar.getTime()) + "：\n" + "您的订单" + number + "卖家" + orders.getFromId() + "已经确认发货，请及时查看物流信息");
        messageMapper.insert(message);
        log.info("通知买家发货成功");
        return "success";
    }


    @Override
    public String refundPhotoUpload(MultipartFile file) {
        log.info("正在上传退款图片描述");
        return OssUtils.uploadPhoto(file,"refundPhoto");
    }
}