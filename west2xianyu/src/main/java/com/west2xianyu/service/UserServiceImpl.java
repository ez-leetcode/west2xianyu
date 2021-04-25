package com.west2xianyu.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.*;
import com.west2xianyu.msg.EvaluateMsg;
import com.west2xianyu.msg.FansMsg;
import com.west2xianyu.msg.HistoryMsg;
import com.west2xianyu.msg.ShoppingMsg;
import com.west2xianyu.pojo.*;
import com.west2xianyu.utils.OssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
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

    @Autowired
    private EvaluateMapper evaluateMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private CommentLikesMapper commentLikesMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MessageMapper messageMapper;

    //有时间加邮箱验证码
    @Override
    public String register(User user) {
        User user1 = userMapper.selectById(user.getId());
        if(user1 != null){
            log.warn("注册失败，该用户已被注册：" + user.getId());
            return "repeatWrong";
        }
        //先对密码加密
        log.info("当前密码：" + user.getPassword());
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        log.info("加密后密码：" + user.getPassword());
        //之前加一个验证码是否正确的验证
        log.info("正在创建新帐号");
        //设置默认邮箱
        user.setEmail(user.getId() + "@fzu.edu.cn");
        //默认用户名是学号
        user.setUsername(user.getId());
        userMapper.insert(user);
        log.info("新账号：" + user.toString());
        if(user.getIsAdministrator() == 1){
            //用户是管理员
            UserRole userRole = new UserRole();
            userRole.setUser(user.getId());
            userRole.setRole("admin");
            userRoleMapper.insert(userRole);
            log.info("创建管理员成功：" + userRole.getUser());
        }
        return "success";
    }


    @Override
    public String changePassword(String id, String oldPassword, String newPassword) {
        User user = userMapper.selectUser(id);
        //无论封号与否都可以修改密码
        if(user == null){
            //用户不存在
            log.warn("修改密码失败，用户不存在：" + id);
            return "existWrong";
        }
        //数据库已经加密过，现在比较
        boolean judge = new BCryptPasswordEncoder().matches(oldPassword,user.getPassword());
        log.info("oldPassword" + oldPassword);
        if(!judge){
            //旧密码错误
            log.warn("修改密码失败，旧密码错误：" + oldPassword);
            return "oldPasswordWrong";
        }
        //更新密码，被冻结也可以更新，记得加密
        String realNewPassword = new BCryptPasswordEncoder().encode(newPassword);
        userMapper.changePassword(id,realNewPassword);
        log.info("密码更新成功：" + newPassword);
        return "success";
    }

    @Override
    public String findPassword(String id, String newPassword) {
        User user = userMapper.selectUser(id);
        //无论封号与否都可以找回密码
        if(user == null){
            //用户不存在
            log.warn("找回密码失败，用户不存在：" + id);
            return "existWrong";
        }
        //加密成密文存入
        String realNewPassword = new BCryptPasswordEncoder().encode(newPassword);
        userMapper.changePassword(id,realNewPassword);
        log.info("找回密码成功：" + newPassword);
        return "success";
    }

    @Override
    public User getUserWhenever(String id) {
        return userMapper.selectUserWhenever(id);
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
        if(!shoppingList.isEmpty()){
            log.info("购物车：" + shoppingList.toString());
        }
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
        shoppingMapper.delete(wrapper);
        log.info("商品移除购物车成功：" + number);
        return "success";
    }

    @Override
    public String addFans(String id, String fansId) {
        User user = userMapper.selectById(id);
        User user1 = userMapper.selectById(fansId);
        //关注用户已经可以登录不用判断是否存在
        if(user == null){
            log.warn("被关注用户不存在，用户：" + id);
            return "existWrong";
        }
        QueryWrapper<Fans> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .eq("fans_id",fansId);
        Fans fans = fansMapper.selectOne(wrapper);
        if(fans != null){
            log.warn("关注失败，用户已被该用户关注");
            return "repeatWrong";
        }
        Fans fans1 = new Fans(id,fansId,null);
        log.info("关注列表更新成功，id：" + id + " fansId：" + fansId);
        fansMapper.insert(fans1);
        //中间要有消息推送，待完成
        Message message = new Message();
        message.setId(id);
        message.setIsRead(0);
        message.setTitle("您有新的粉丝关注哦~");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        message.setMsg(dateFormat.format(calendar.getTime()) + "：\n" + "您有新的粉丝关注您啦！用户：" + user1.getUsername() + "（ " + user1.getId() + "）");
        //发送消息
        messageMapper.insert(message);
        //修改粉丝数和关注数
        user.setFansCounts(user.getFansCounts() + 1);
        user1.setFollowCounts(user1.getFollowCounts() + 1);
        userMapper.updateById(user);
        log.info("更新被关注者粉丝数量成功：" + user.getFansCounts());
        userMapper.updateById(user1);
        log.info("更新关注者关注数量成功：" + user1.getFansCounts());
        log.info("添加关注成功，用户：" + fansId + " 被关注者：" + id);
        return "success";
    }

    @Override
    public String deleteFans(String id, String fansId) {
        QueryWrapper<Fans> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .eq("fans_id",fansId);
        Fans fans = fansMapper.selectOne(wrapper);
        if(fans == null){
            log.warn("取消关注失败，用户未被关注：" + id);
            return "existWrong";
        }
        fansMapper.delete(wrapper);
        User user = userMapper.selectById(id);
        int cnt = user.getFansCounts() - 1;
        user.setFansCounts(cnt);
        //更新被关注用户粉丝数
        userMapper.updateById(user);
        log.info("更新被关注用户粉丝数成功，id：" + id + " 粉丝数：" + cnt);
        User user1 = userMapper.selectById(fansId);
        int cnt1 = user1.getFollowCounts() - 1;
        user1.setFollowCounts(cnt1);
        userMapper.updateById(user1);
        log.info("更新用户关注数成功，id：" + id + "粉丝数：" + cnt1);
        log.info("成功取消关注，用户：" + fansId + "被关注者：" + id);
        return "success";
    }

    @Override
    public JSONObject getFollow(String id, long cnt, long page) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Fans> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .orderByDesc("create_time");
        Page<Fans> page1 = new Page<>(page,cnt);
        fansMapper.selectPage(page1,wrapper);
        //获取粉丝集合
        List<Fans> fansList = page1.getRecords();
        List<FansMsg> fansMsgList = new LinkedList<>();
        log.info("粉丝列表：" + fansList.toString());
        for(Fans x:fansList){
            //遍历粉丝集合，查询所有粉丝信息
            User user = userMapper.selectById(x.getFansId());
            QueryWrapper<Orders> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("from_id",id)
                    .eq("to_id",x.getFansId());
            //获取购买列表
            List<Orders> ordersList = ordersMapper.selectList(wrapper1);
            FansMsg fansMsg = new FansMsg(id,x.getId(),user.getUsername(),user.getPhoto(),
                    user.getIntroduction(), ordersList.size(),x.getCreateTime());
            fansMsgList.add(fansMsg);
        }
        jsonObject.put("fansList",fansMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        log.info("获取粉丝列表成功：" + fansMsgList.toString());
        return jsonObject;
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
        //评论消息推送
        Message message = new Message();
        message.setIsRead(0);
        message.setId(goods.getFromId());
        message.setTitle("有一个用户新评论了您的商品：" + goods.getGoodsName());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        message.setMsg(dateFormat.format(calendar.getTime()) + "：\n" + "您的商品：" + goods.getGoodsName() + "被用户：" + user.getUsername() +
                "评论，评论内容： \n" + comments);
        log.info("评论成功，用户：" + id + " 评论："+ comments);
        return "success";
    }

    @Override
    public String deleteComment(Long goodsId, String id, String comments, String createTime) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",goodsId)
                .eq("id",id)
                .eq("comments",comments)
                .eq("create_time",createTime);
        Comment comment = commentMapper.selectOne(wrapper);
        if(comment == null){
            log.info("删除评论失败，评论不存在：" + comments);
            return "existWrong";
        }
        commentMapper.delete(wrapper);
        log.info("删除评论成功，用户：" + goodsId + " 评论：" + comments);
        return "success";
    }

    @Override
    public String addFeedback(String id,String phone,String feedbacks,String title) {
        feedbackMapper.insert(new Feedback(null,id,phone,title,feedbacks,0,null));
        log.info("用户反馈成功，用户：" + id + " 反馈：" + feedbacks);
        return "success";
    }

    //商品冻结检查和id检查暂时不做
    @Override
    public String addLikes(Long goodsId, String id, String comments, String createTime) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",goodsId)
                .eq("id",id)
                .eq("comments",comments)
                .eq("create_time",createTime);
        Comment comment = commentMapper.selectOne(wrapper);
        if(comment == null){
            log.warn("点赞失败，评论不存在");
            return "existWrong";
        }
        QueryWrapper<CommentLikes> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("goods_id",goodsId)
                .eq("id",id)
                .eq("comments",comments);
        CommentLikes commentLikes = commentLikesMapper.selectOne(wrapper1);
        if(commentLikes != null){
            log.warn("点赞失败，评论已被点赞");
            return "repeatWrong";
        }
        CommentLikes commentLikes1 = new CommentLikes(goodsId,comments,id,comment.getCreateTime(),null);
        commentLikesMapper.insert(commentLikes1);
        log.info("插入点赞信息成功：" + commentLikes1.toString());
        comment.setLikes(comment.getLikes() + 1);
        commentMapper.updateById(comment);
        log.info("更新（增加）点赞数据成功");
        return "success";
    }

    @Override
    public String deleteLikes(Long goodsId, String id, String comments, String createTime) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",goodsId)
                .eq("id",id)
                .eq("comments",comments)
                .eq("create_time",createTime);
        Comment comment = commentMapper.selectOne(wrapper);
        if(comment == null){
            log.warn("取消点赞失败，评论不存在");
            return "existWrong";
        }
        QueryWrapper<CommentLikes> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("goods_id",goodsId)
                .eq("id",id)
                .eq("comments",comments);
        CommentLikes commentLikes = commentLikesMapper.selectOne(wrapper1);
        if(commentLikes == null){
            log.warn("取消点赞失败，评论未被点赞");
            return "repeatWrong";
        }
        commentLikesMapper.delete(wrapper1);
        log.info("删除点赞信息成功");
        comment.setLikes(comment.getLikes() - 1);
        commentMapper.updateById(comment);
        log.info("更新（减少）点赞数据成功");
        return "success";
    }

    @Override
    public String uploadPhoto(MultipartFile file, String id) {
        /*
        if(file.isEmpty()){
            log.warn("上传头像失败，头像文件为空");
            return "emptyWrong";
        }
        //获取文件名
        String fileName = file.getOriginalFilename();
        if(fileName == null){
            log.warn("上传头像失败，文件名为空");
            return "emptyWrong";
        }
        //获取上传头像文件类型
        String suffixName = fileName.substring(fileName.lastIndexOf(".") + 1);
        log.info("上传头像文件类型：" + suffixName);
        //暂时只支持jpg png jpeg类型头像文件上传
        if(!(suffixName.equals("jpg") || suffixName.equals("jpeg") || suffixName.equals("png"))){
            log.warn("上传头像失败，文件格式不匹配");
        }
        //新文件名，加UUID防止重复
        String fileNewName = UUID.randomUUID().toString() + fileName;
        log.info("新头像文件名：" + fileNewName);
        //文件路径
        String filePath = "/xy/photo/" + fileNewName;
        log.info("头像文件路径：" + filePath);
        log.info("正在上传头像，用户：" + id);
        //封装上传文件全路径
        File photoFile = new File(filePath);
        try{
            //保存头像图片
            file.transferTo(photoFile);
        }catch (IOException e){
            e.printStackTrace();
            log.warn("服务器错误，头像上传失败");
            return "internetWrong";
        }
        */
        //先尝试获取用户信息
        User user = userMapper.selectById(id);
        if(user == null){
            log.warn("上传头像失败，用户不存在：" + id);
            return "existWrong";
        }
        //现在使用oss存储
        String url = OssUtils.uploadPhoto(file,"userPhoto");
        if(url.length() < 12){
            //少于12说明报错
            return url;
        }
        //上传成功后先删除源文件
        if(user.getPhoto() != null){
            //删除原头像文件
            log.info("正在删除原头像文件：" + user.getPhoto());
            String lastObjectName = user.getPhoto().substring(user.getPhoto().lastIndexOf("/") + 1);
            log.info("原文件名：" + lastObjectName);
            OssUtils.deletePhoto(lastObjectName,"userPhoto");
        }
        user.setPhoto(url);
        userMapper.updateById(user);
        log.info("更新头像资源路径成功：" + user.getPhoto());
        return user.getPhoto();
    }

    @Override
    public String addAddress(String id, String campus, String realAddress, String name, String phone, int isDefault) {
        Address address = new Address(null,id,campus,realAddress,name,phone,null,null,null);
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .eq("campus",campus)
                .eq("real_address",realAddress)
                .eq("name",name)
                .eq("phone",phone);
        List<Address> addressList = addressMapper.selectList(wrapper);
        if(!addressList.isEmpty()){
            log.warn("添加地址失败，地址信息重复：" + addressList.toString());
            return "repeatWrong";
        }
        addressMapper.insert(address);
        log.info("地址设置插入成功：" + address.toString());
        if(isDefault == 1){
            //有设置为默认地址选项，把默认地址改掉
            User user = userMapper.selectById(id);
            if(user == null){
                log.info("修改默认地址失败，用户不存在：" + id);
                return "existWrong";
            }
            user.setAddress(addressMapper.selectOne(wrapper).getNumber());
            userMapper.updateById(user);
            log.info("默认地址修改成功：" + user.getAddress());
        }
        return "success";
    }

    @Override
    public String deleteAddress(Long number, String id) {
        Address address = addressMapper.selectById(number);
        if(address == null){
            log.warn("删除地址设置失败，地址不存在");
            return "existWrong";
        }
        User user = userMapper.selectById(id);
        if(user == null){
            log.warn("删除地址设置失败，用户不存在");
            return "userWrong";
        }
        //当前默认地址编号
        Long number1 = user.getAddress();
        if(number1.equals(number)){
            log.info("发现删除地址为默认地址");
            QueryWrapper<Address> wrapper = new QueryWrapper<>();
            wrapper.eq("id",id);
            List<Address> addressList = addressMapper.selectList(wrapper);
            if(addressList.size() <= 1){
                log.warn("没有其他地址，至少保留一个地址");
                return "addressWrong";
            }
            //删除地址信息
            addressMapper.deleteById(number);
            //取最近的地址配置为默认
            wrapper.orderByDesc("create_time");
            List<Address> addressList1 = addressMapper.selectList(wrapper);
            Address address1 = addressList1.get(0);
            log.info("正在把其他地址设为默认");
            if(address1 != null){
                //如果还有别的地址，把其设为默认
                User user1 = new User();
                user1.setId(id);
                user1.setAddress(address1.getNumber());
                userMapper.updateById(user1);
            }
        }
        //删除地址信息
        addressMapper.deleteById(number);
        log.info("删除地址信息成功");
        return "success";
    }

    //在冻结商品的时候就要把购物车里的删了！！！！！！！！！！！！！！！！
    @Override
    public JSONObject getShopping(String id, long cnt, long page) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Shopping> wrapper = new QueryWrapper<>();
        //desc  大到小排序   asc 小到大
        wrapper.eq("id",id)
                .orderByDesc("create_time");
        Page<Shopping> page1 = new Page<>(page,cnt);
        shoppingMapper.selectPage(page1,wrapper);
        List<Shopping> shoppingList = page1.getRecords();
        List<ShoppingMsg> shoppingMsgList = new LinkedList<>();
        //获取用户实例，以获取用户校区信息
        User user = userMapper.selectById(id);
        for(Shopping x:shoppingList){
            //获取购物车商品实例
            log.info(x.getNumber().toString());
            Goods goods = goodsMapper.selectGoodsWhenever(x.getNumber());
            //获取卖家实例
            User user1 = userMapper.selectUser(goods.getFromId());
            double freight = 6.0;
            if(user.getCampus().equals(user1.getCampus())){
                //校区一样，免运费
                freight = 0.0;
            }
            //插入新的购物车信息
            shoppingMsgList.add(new ShoppingMsg(x.getNumber(),goods.getFromId(),goods.getPrice(),freight,
                    goods.getGoodsName(),goods.getDescription(),goods.getPhoto(),x.getCreateTime()));
        }
        jsonObject.put("shoppingList",shoppingMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        log.info("获取购物车信息成功");
        log.info("页面数：" + page1.getPages());
        //log.info("购物车信息：" + shoppingMsgList.toString());
        return jsonObject;
    }


    @Override
    public String changeAddress(Address address) {
        //这里不会去验证用户和地址编号是否匹配
        Address address1 = addressMapper.selectById(address.getNumber());
        if(address1 == null){
            log.warn("修改地址错误，地址设置不存在");
            return "existWrong";
        }
        addressMapper.updateById(address);
        log.info("修改地址设置成功：" +address1.toString());
        return "success";
    }

    @Override
    public JSONObject getAddress(String id, long cnt, long page) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .orderByDesc("create_time");
        Page<Address> page1 = new Page<>(page,cnt);
        addressMapper.selectPage(page1,wrapper);
        List<Address> addressList = page1.getRecords();
        jsonObject.put("addressList",addressList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        return jsonObject;
    }

    //封装成historyMsg，4.19 到这
    @Override
    public JSONObject getHistory(String id,long cnt,long page) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<History> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .orderByDesc("update_time");
        Page<History> page1 = new Page<>(page,cnt);
        historyMapper.selectPage(page1,wrapper);
        List<History> historyList = page1.getRecords();
        List<HistoryMsg> historyMsgList = new ArrayList<>();
        for(History x:historyList){
            Goods goods = goodsMapper.selectGoodsWhenever(x.getGoodsId());
            historyMsgList.add(new HistoryMsg(goods.getNumber(),id,goods.getGoodsName(),goods.getPrice(),goods.getPhoto(),goods.getUpdateTime()));
        }
        jsonObject.put("historyList",historyMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        log.info("获取历史记录信息成功");
        log.info("页面数：" + page1.getPages());
        log.info("历史记录信息：" + historyList.toString());
        return jsonObject;
    }


    @Override
    public String deleteHistory(Long goodsId, String id) {
        QueryWrapper<History> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",goodsId)
                .eq("id",id);
        int result = historyMapper.delete(wrapper);
        if(result != 1){
            log.warn("删除历史记录失败，可能是历史记录不存在：" + goodsId);
            return "existWrong";
        }
        log.info("删除历史记录成功");
        return "success";
    }

    @Override
    public String deleteAllHistory(String id) {
        QueryWrapper<History> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        int result = historyMapper.delete(wrapper);
        if(result == 0){
            log.warn("清空历史浏览异常，历史浏览可能已经被清除");
            return "existWrong";
        }else{
            log.info("清空历史浏览成功，用户：" + id + " 清除条数：" + result);
        }
        return "success";
    }

    @Override
    public JSONObject getMessage(String id, long cnt, long page, int isRead) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        JSONObject jsonObject = new JSONObject();
        Page<Message> page1 = new Page<>(page,cnt);
        wrapper.eq("id",id)
                //根据时间排序
                .orderByDesc("create_time");
        if(isRead == 1){
            //查询已读
            wrapper.eq("is_read",1);
        }else{
            //查询未读
            wrapper.eq("is_read",0);
        }
        messageMapper.selectPage(page1,wrapper);
        List<Message> messageList = page1.getRecords();
        jsonObject.put("messageList",messageList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        log.info("获取消息盒子信息成功：" + id);
        return jsonObject;
    }


    @Override
    public JSONObject getOneMessage(String id, Long number) {
        JSONObject jsonObject = new JSONObject();
        Message message = messageMapper.selectById(number);
        if(message == null){
            //通知不存在
            log.warn("通知不存在：" + number);
            return null;
        }
        //设置已读
        message.setIsRead(1);
        messageMapper.updateById(message);
        log.info("已读更新成功");
        jsonObject.put("message",message);
        return jsonObject;
    }

    @Override
    public String readAllMessage(String id) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                //未读
                .eq("is_read",0);
        List<Message> messageList = messageMapper.selectList(wrapper);
        if(messageList.isEmpty()){
            //没有要已读的可能前台连续申请，返回existWrong
            log.warn("已读所有消息：" + id);
            return "existWrong";
        }
        //更新所有消息为已读
        for(Message x:messageList){
            x.setIsRead(1);
            messageMapper.updateById(x);
        }
        log.info("更新所有消息为已读成功：" + id);
        return "success";
    }

    @Override
    public String deleteAllShopping(String id) {
        QueryWrapper<Shopping> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        List<Shopping> shoppingList = shoppingMapper.selectList(wrapper);
        if(shoppingList.isEmpty()){
            //购物车已被清空
            log.warn("购物车已被清空：" + id);
            return "existWrong";
        }
        //购物车未被清空
        shoppingMapper.delete(wrapper);
        log.info("清空购物车成功：" + id);
        return "success";
    }

    @Override
    public JSONObject getEvaluate(String id, long cnt, long page) {
        JSONObject jsonObject = new JSONObject();
        //用户账号被冻结也可以查到
        Page<Evaluate> page1 = new Page<>(page,cnt);
        QueryWrapper<Evaluate> wrapper = new QueryWrapper<>();
        //注意是卖家出售货物
        wrapper.eq("from_id",id)
                .orderByDesc("create_time");
        //获取评价
        evaluateMapper.selectPage(page1,wrapper);
        List<Evaluate> evaluateList = page1.getRecords();
        List<EvaluateMsg> evaluateMsgList = new LinkedList<>();
        for(Evaluate x:evaluateList){
            //获取买家信息
            User user = userMapper.selectUser(x.getToId());
            evaluateMsgList.add(new EvaluateMsg(x.getNumber(),x.getFromId(),x.getToId(),user.getUsername(),user.getPhoto(),x.getPhoto(),
                    x.getEvaluation(),x.getDescribe(),x.getService(),x.getLogistics(),x.getIsNoname(),x.getCreateTime()));
        }
        log.info("获取用户评价列表成功：" + id);
        jsonObject.put("evaluateList",evaluateMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        return jsonObject;
    }
}