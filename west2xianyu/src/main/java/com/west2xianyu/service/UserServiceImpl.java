package com.west2xianyu.service;


import com.west2xianyu.mapper.CommentMapper;
import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.mapper.ShoppingMapper;
import com.west2xianyu.mapper.UserMapper;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.pojo.Shopping;
import com.west2xianyu.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        //已被添加购物车待完成！
        Shopping shopping = new Shopping(number,Id,null);
        shoppingMapper.insert(shopping);
        log.info("添加购物车成功，商品：" + shopping.toString());
        return "success";
    }
}