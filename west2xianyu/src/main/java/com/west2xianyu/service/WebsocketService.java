package com.west2xianyu.service;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.mapper.TalkMapper;
import com.west2xianyu.pojo.Talk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
@ServerEndpoint(value = "/websocket/{id}")
public class WebsocketService {


    @Autowired
    private TalkMapper talkMapper;

    //与某个客户端的连接会话，以此来给客户端发送数据
    private Session session;

    //线程安全hashmap，存放每个客户端对应的websocket对象
    private static ConcurrentHashMap<String,WebsocketService> websocketServiceConcurrentHashMap = new ConcurrentHashMap<>();


    //建立连接调用方法
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id){
        log.info("正在建立连接，用户：" + id);
        this.session = session;
        websocketServiceConcurrentHashMap.put(id,this);
        log.info("有新的连接，当前连接总数：" + websocketServiceConcurrentHashMap.size());
    }


    //收到客户端消息后调用方法
    @OnMessage
    public void onMessage(String message,Session session, @PathParam("id") String id){
        log.info("正在发送消息，用户：" + id + " 客户端id：" + session.getId());
        log.info("消息：" + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        Talk talk = new Talk(jsonObject.getString("fromId"),jsonObject.getString("toId"),jsonObject.getString("message"),0,null);
        //推送信息
        insertTalk(talk.getFromId(),talk.getToId(),message);
    }


    //关闭连接调用方法
    @OnClose
    public void onClose(@PathParam("id") String id){
        log.info("正在关闭连接：" + id);
        websocketServiceConcurrentHashMap.remove(id);
        log.info("连接已断开，当前连接数：" + websocketServiceConcurrentHashMap.size());
    }



    //聊天出现错误时调用
    @OnError
    public void onError(Session session, Throwable error){
        log.info("聊天出现错误：" + session.getId());
        error.printStackTrace();
    }


    //消息推送，同时保存聊天记录
    public void insertTalk(String fromId,String toId,String message){
        //先尝试获取该用户websocket，看看存不存在，没有就留个聊天记录就好
        WebsocketService websocketService = websocketServiceConcurrentHashMap.get(toId);
        if(websocketService != null){
            //该用户在线，非阻塞式推送
            websocketService.session.getAsyncRemote().sendText(message);
        }
        //更新聊天记录
        log.info("正在更新聊天记录，fromId：" + fromId + " toId：" + toId + " message：" + message);
        //标记为未读
        talkMapper.insert(new Talk(fromId,toId,message,0,null));
        log.info("更新聊天记录成功");
    }



}
