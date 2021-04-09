package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.FeedbackMapper;
import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.msg.FeedbackMsg;
import com.west2xianyu.msg.UserMsg;
import com.west2xianyu.pojo.Feedback;
import com.west2xianyu.pojo.User;
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
        jsonObject.put("count",page1.getSize());
        return jsonObject;
    }

    @Override
    public JSONObject getFeedback(String id, Long number) {
        JSONObject jsonObject = new JSONObject();
        Feedback feedback = feedbackMapper.selectById(number);
        User user = userMapper.selectById(feedback.getId());
        feedback.setIsRead(1);
        FeedbackMsg feedbackMsg = new FeedbackMsg(number,user.getId(),user.getUsername(),user.getPhoto(),
                feedback.getTitle(),feedback.getFeedbacks(),1,feedback.getCreateTime());
        log.info("获取详细反馈信息成功：" + feedbackMsg.toString());
        jsonObject.put("feedbackMsg",feedbackMsg);
        //更新为已读
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
            jsonObject.put("count",page1.getSize());
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
}