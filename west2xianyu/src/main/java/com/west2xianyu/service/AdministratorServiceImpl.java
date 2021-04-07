package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.FeedbackMapper;
import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.msg.FeedbackMsg;
import com.west2xianyu.pojo.Feedback;
import com.west2xianyu.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
