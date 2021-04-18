package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.*;
import com.west2xianyu.msg.FeedbackMsg;
import com.west2xianyu.msg.GoodsMsg;
import com.west2xianyu.msg.RefundMsg;
import com.west2xianyu.msg.UserMsg;
import com.west2xianyu.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class AdministratorServiceImpl implements AdministratorService{


    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private RefundMapper refundMapper;


    @Override
    public JSONObject getAllFeedback(String id, Long cnt, Long page,int isHide) {
        JSONObject jsonObject = new JSONObject();
        Page<Feedback> page1 = new Page<>(page,cnt);
        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        if(isHide == 1){
            log.info("隐藏所有已读");
            wrapper.eq("is_read",0);
        }
        feedbackMapper.selectPage(page1,wrapper);
        List<Feedback> feedbackList = page1.getRecords();
        List<FeedbackMsg> feedbackMsgList = new LinkedList<>();
        for(Feedback x:feedbackList){
            //先获取用户实例
            User user = userMapper.selectById(x.getId());
            //更新消息
            feedbackMsgList.add(new FeedbackMsg(x.getNumber(),x.getId(),user.getUsername(),user.getPhoto(),x.getTitle(),
                    null,x.getIsRead(),x.getCreateTime()));
        }
        log.info("获取全部反馈信息成功：" + id);
        jsonObject.put("feedbackList",feedbackMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        return jsonObject;
    }

    @Override
    public JSONObject getFeedback(String id, Long number) {
        JSONObject jsonObject = new JSONObject();
        Feedback feedback = feedbackMapper.selectById(number);
        User user = userMapper.selectById(feedback.getId());
        FeedbackMsg feedbackMsg = new FeedbackMsg(number,user.getId(),user.getUsername(),user.getPhoto(),
                feedback.getTitle(),feedback.getFeedbacks(),feedback.getIsRead(),feedback.getCreateTime());
        log.info("获取详细反馈信息成功：" + feedbackMsg.toString());
        jsonObject.put("feedbackMsg",feedbackMsg);
        //更新为已读
        feedback.setIsRead(1);
        feedbackMapper.updateById(feedback);
        log.info("更新为已读成功");
        return jsonObject;
    }

    @Override
    public JSONObject getAllUser(String keyword, Long cnt, Long page) {
        JSONObject jsonObject = new JSONObject();
        //因为被逻辑删除的mybatis-plus不能直接查询，所以我们手动查询并分页
        List<User> userList;
        long a = (page - 1) * cnt;
        long b = cnt;
        if(keyword != null){
            //根据关键词获取所有user（包括被冻结的）
            userList = userMapper.getAllUser(keyword,a,b);
        }else{
            //获取全部（包括被冻结的）
            userList = userMapper.getAllUer1(a,b);
        }
        List<User> userList1;
        if(keyword != null){
            userList1 = userMapper.selectAll1(keyword);
        }else{
            userList1 = userMapper.selectAll();
        }
        long sum = userList1.size();
        long i = sum % cnt;
        long pages = sum / cnt;
        if(i != 0){
            pages ++;
        }
        List<UserMsg> userMsgList = new LinkedList<>();
        for(User x:userList){
            userMsgList.add(new UserMsg(x.getId(),x.getCreateTime(),x.getDeleted(),x.getFrozenCounts(),x.getFrozenDate(),x.getReopenDate()));
        }
        log.info("获取用户信息成功：" + userMsgList.toString());
        jsonObject.put("userList",userMsgList);
        jsonObject.put("pages",pages);
        jsonObject.put("count",userList1.size());
        return jsonObject;
    }

    @Override
    public JSONObject getAllUser1(int isDeleted, String keyword, Long cnt, Long page) {
        JSONObject jsonObject = new JSONObject();
        if(isDeleted == 0){
            //正常账号
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            Page<User> page1 = new Page<>(page,cnt);
            wrapper.orderByDesc("create_time");
            if(keyword != null){
                wrapper.like("id",keyword);
            }
            userMapper.selectPage(page1,wrapper);
            List<User> userList = page1.getRecords();
            List<UserMsg> userMsgList = new LinkedList<>();
            for(User user:userList){
                userMsgList.add(new UserMsg(user.getId(),user.getCreateTime(),user.getDeleted(),user.getFrozenCounts(),user.getFrozenDate(),user.getReopenDate()));
            }
            log.info("获取正常账号信息成功：" + userMsgList.toString());
            jsonObject.put("userList",userMsgList);
            jsonObject.put("pages",page1.getPages());
            jsonObject.put("count",page1.getTotal());
        }else{
            //已被冻结帐号
            List<User> userList;
            List<User> userList1;
            List<UserMsg> userMsgList = new LinkedList<>();
            if(keyword != null){
                //有搜索关键词，先获取全部用户信息便于分页
                userList = userMapper.selectDeletedUser(keyword);
            }else{
                //无搜索关键词
                userList = userMapper.selectDeletedUser1();
            }
            long sum = userList.size();
            long i = sum % cnt;
            long pages = sum / cnt;
            if(i != 0){
                pages ++;
            }
            if(keyword != null){
                userList1 = userMapper.selectDeletedUser2(keyword,(page - 1) * cnt,cnt);
            }else{
                userList1 = userMapper.selectDeletedUser3((page - 1) * cnt,cnt);
            }
            for(User user:userList1){
                userMsgList.add(new UserMsg(user.getId(),user.getCreateTime(),user.getDeleted(),user.getFrozenCounts(),user.getFrozenDate(),user.getReopenDate()));
            }
            log.info("获取冻结账号信息成功：" + userMsgList.toString());
            jsonObject.put("userList",userMsgList);
            jsonObject.put("pages",pages);
            jsonObject.put("count",sum);
        }
        return jsonObject;
    }

    @Override
    public User frozeUser(String id, String reason, int days) {
        User user = userMapper.selectById(id);
        //获取当前时间，可能要设置时区
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        if(user == null){
            log.warn("封号失败，帐号可能已经被封号：" + id);
            return null;
        }else{
            //设置冻结日期
            user.setFrozenDate(date);
            //加上对应时间
            calendar.add(Calendar.DAY_OF_YEAR,days);
            date = calendar.getTime();
            //解封时间
            user.setReopenDate(date);
            //设置封号
            user.setDeleted(1);
            userMapper.updateById(user);
            log.info("封号成功，账号：" + id + " 时间：" + days);
        }
        //封号之后，因为用户登录不上去收不到通知，用邮件通知用户
        return user;
    }


    @Override
    public String reopenUser(String id, String adminId) {
        User user = userMapper.selectById(id);
        if(user != null){
            log.warn("解封失败，用户并未被封号：" + id);
            return "userWrong";
        }
        //用户已被封禁情况下，获取用户信息
        User user1 = userMapper.selectUserWhenever(id);
        user1.setDeleted(0);
        //管理员解封会清除违规次数
        user1.setFrozenCounts(0);
        //更新用户信息
        userMapper.updateById(user1);
        log.info("用户解封成功：" + userMapper.selectById(id).toString());
        //邮件告知用户
        return "success";
    }


    @Override
    public JSONObject getGoodsList(String keyword, Long cnt, Long page) {
        JSONObject jsonObject = new JSONObject();
        Page<Goods> page1 = new Page<>(page,cnt);
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        if(keyword != null){
            wrapper.like("number",keyword);
        }
        //根据商品状态排序（可能要排除状态，待完成）
        wrapper.orderByDesc("status");
        List<GoodsMsg> goodsMsgList = new LinkedList<>();
        goodsMapper.selectPage(page1,wrapper);
        List<Goods> goodsList = page1.getRecords();
        for(Goods x:goodsList){
            goodsMsgList.add(new GoodsMsg(x.getNumber(),x.getFromId(),x.getPrice(),x.getPhoto(),x.getGoodsName(),x.getDescription(),x.getScanCounts(),
                    x.getFavorCounts(),x.getUpdateTime()));
        }
        log.info("获取商品列表成功：" + goodsMsgList.toString());
        jsonObject.put("goodsList",goodsMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        return jsonObject;
    }


    //审核失败状态 9
    @Override
    public String judgeGoods(Long number, String id,int isPass) {
        //判断更新审核情况
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("审核失败，订单不存在：" + number);
            return "existWrong";
        }
        if(orders.getStatus() != 1){
            log.warn("审核失败，订单状态有误：" + orders.getStatus());
            return "statusWrong";
        }
        if(isPass == 1){
            //审核通过，更新状态，通知卖家
            orders.setStatus(2);
            ordersMapper.updateById(orders);
            //通知用户待完成
        }else{
            //审核不通过，更新状态，更新卖家犯罪次数，通知卖家
            orders.setStatus(9);
            ordersMapper.updateById(orders);
            User user = userMapper.selectById(orders.getFromId());
            if(user != null){
                //用户已被封禁，就不管他了
                user.setFrozenCounts(user.getFrozenCounts() + 1);
                userMapper.updateById(user);
                log.info("更新用户犯罪次数成功：" + user.getId());
            }
            //通知用户待完成
        }
        log.info("审核商品成功：" + number);
        return "success";
    }


    //10：申请退款 11：退款成功 12：退款失败
    @Override
    public String judgeRefund(Long number, String id, int isPass) {
        Orders orders = ordersMapper.selectById(number);
        Refund refund = refundMapper.selectById(number);
        if(orders == null || refund == null){
            log.warn("处理退款失败，订单失效或没有退款：" + number);
            return "existWrong";
        }
        if(orders.getStatus() != 10){
            //不是处于申请退款状态
            log.warn("处理退款失败，订单状态不符合要求：" + number);
            return "statusWrong";
        }
        //符合要求情况
        if(isPass == 1){
            //同意退款
            orders.setStatus(11);
            ordersMapper.updateById(orders);
            log.info("退款申请成功，管理员：" + id  + " 订单：" + number);
        }else{
            //不同意退款，恢复原来的订单状态
            if(orders.getSendTime() == null){
                //未发货
                orders.setStatus(3);
            }else{
                //已发货
                orders.setStatus(4);
            }
            ordersMapper.updateById(orders);
            log.info("退款申请失败，管理员：" + id + "订单：" + number);
        }
        //处理完申请伪删除退款数据
        refund.setDeleted(1);
        refundMapper.updateById(refund);
        //通知买卖双方
        return "success";
    }

    @Override
    public JSONObject getRefund(Long number) {
        JSONObject jsonObject = new JSONObject();
        Orders orders = ordersMapper.selectById(number);
        Refund refund = refundMapper.selectById(number);
        if(orders == null || refund == null){
            log.warn("获取退款订单信息失败，订单或退款信息不存在：" + number);
            jsonObject.put("getRefundStatus","existWrong");
            return jsonObject;
        }
        if(orders.getStatus() != 10){
            //不是处于申请退款状态
            log.warn("处理退款失败，订单状态不符合要求：" + orders.getStatus());
            jsonObject.put("getRefundStatus","statusWrong");
        }else{
            //状态没问题
            RefundMsg refundMsg = new RefundMsg(refund.getNumber(),refund.getToId(),orders.getFromId(),refund.getMoney(),
                    refund.getReason(),refund.getDescription(),refund.getPhoto(),refund.getCreateTime());
            jsonObject.put("refund",refundMsg);
            log.info("获取退款信息成功：" + refundMsg.toString());
        }
        jsonObject.put("getRefundStatus","success");
        return jsonObject;
    }
}