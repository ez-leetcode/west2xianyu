package com.west2xianyu.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.west2xianyu.mapper.*;
import com.west2xianyu.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private ShoppingMapper shoppingMapper;

    @Autowired
    private FansMapper fansMapper;


    //有时间加邮箱验证码
    @Override
    public String register(User user) {
        User user1 = userMapper.selectById(user.getId());
        if(user1 != null){
            log.warn("注册失败，该用户已被注册：" + user.getId());
            return "repeatWrong";
        }
        //之前加一个验证码是否正确的验证
        log.info("正在创建新帐号");
        //设置默认邮箱
        user.setEmail(user.getId() + "@fzu.edu.cn");
        //默认用户名是学号
        user.setUsername(user.getId());
        userMapper.insert(user);
        log.info("新账号：" + user.toString());
        return "success";
    }

    @Override
    public String login(User user) {
        User user1 = userMapper.selectById(user.getId());
        if(user1 == null){
            log.warn("登录失败，用户不存在：" + user.getId());
            return "existWrong";
        }
        return "success";
    }

    @Override
    public User getUser(String Id) {
        return userMapper.selectById(Id);
    }

    @Override
    public int saveUser(User user){
        return userMapper.updateById(user);
    }


    //用户名默认存在，不检查了，太麻烦


    @Override
    public String addShopping(Long number, String Id) {
        Goods goods = goodsMapper.selectById(number);
        if(goods == null){
            log.warn("添加购物车失败，该商品不存在：" + number);
            return "existWrong";
        }
        if(goods.getIsFrozen() == 1){
            log.warn("添加购物车失败，该商品已被冻结：" + number);
            return  "frozenWrong";
        }
        QueryWrapper<Shopping> wrapper = new QueryWrapper<>();
        wrapper.eq("number",number);
        wrapper.eq("id",Id);
        List<Shopping> shoppingList = shoppingMapper.selectList(wrapper);
        log.info("购物车：" + shoppingList.toString());
        if(shoppingList.size() != 0){
            log.warn("添加购物车失败，该商品已被收藏");
            return "repeatWrong";
        }
        //已被添加购物车待完成！
        Shopping shopping = new Shopping(number,Id,null);
        shoppingMapper.insert(shopping);
        log.info("添加购物车成功，商品：" + shopping.toString());
        return "success";
    }

    @Override
    public String deleteShopping(Long number, String Id) {
        //移除购物车不用考虑商品是否存在
        QueryWrapper<Shopping> wrapper = new QueryWrapper<>();
        wrapper.eq("number",number);
        wrapper.eq("id",Id);
        List<Shopping> shoppingList = shoppingMapper.selectList(wrapper);
        if(shoppingList.size() == 0){
            log.warn("移除购物车失败，该商品不在购物车：" + number);
            return "existWrong";
        }
        int result = shoppingMapper.delete(wrapper);
        if(result != 1){
            return "unknownWrong";
        }
        log.info("商品移除购物车成功：" + number);
        return "success";
    }

    @Override
    public String addFans(String id, String fansId) {
        User user = userMapper.selectById(id);
        if(user == null){
            log.warn("被关注用户不存在，用户：" + id);
            return "existWrong";
        }
        log.info("正在添加关注列表");
        Fans fans = new Fans(id,fansId,null);
        int result = fansMapper.insert(fans);
        if(result != 1){
            return "unknownWrong";
        }
        //中间要有消息推送，待完成
        log.info("添加关注成功，用户：" + fansId + " 被关注者：" + id);
        return "success";
    }

    @Override
    public String addComment(Long goodsId,String id,String comments) {
        Goods goods = goodsMapper.selectById(goodsId);
        if(goods == null){
            log.warn("评论失败，商品不存在或已被冻结");
            return "frozenWrong";
        }
        User user = userMapper.selectById(id);
        if(user == null){
            log.warn("评论失败，用户id不存在或已被冻结");
            return "userWrong";
        }
        Comment comment = new Comment(goodsId,comments,id,user.getUsername(),null,null,null);
        commentMapper.insert(comment);
        log.info("评论成功：" + comments);
        return "success";
    }
}