package com.west2xianyu.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.*;
import com.west2xianyu.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
    private FeedbackMapper feedbackMapper;

    @Autowired
    private CommentLikesMapper commentLikesMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private HistoryMapper historyMapper;


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
    public String deleteFans(String id, String fansId) {
        QueryWrapper<Fans> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .eq("fans_id",fansId);
        Fans fans = fansMapper.selectOne(wrapper);
        if(fans == null){
            log.warn("取消关注失败，用户未被关注：" + id);
            return "existWrong";
        }
        int result = fansMapper.delete(wrapper);
        if(result != 1){
            log.warn("服务器错误");
            return "unknownWrong";
        }
        User user = userMapper.selectById(id);
        int cnt = user.getFansCounts() - 1;
        user.setFansCounts(cnt);
        //更新被关注用户粉丝数
        userMapper.updateById(user);
        log.info("更新被关注用户粉丝数成功，id：" + id + " 粉丝数：" + cnt);
        User user1 = userMapper.selectById(fansId);
        int cnt1 = user1.getFansCounts() - 1;
        user1.setFollowCounts(cnt1);
        userMapper.updateById(user1);
        log.info("更新用户关注数成功，id：" + id + "粉丝数：" + cnt1);
        log.info("成功取消关注，用户：" + fansId + "被关注者：" + id);
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
        int result = commentMapper.delete(wrapper);
        if(result != 1){
            log.warn("服务器错误");
            return "unknownWrong";
        }
        log.info("删除评论成功，用户：" + goodsId + " 评论：" + comments);
        return "success";
    }

    @Override
    public String addFeedback(String id,String phone,String feedbacks) {
        int result = feedbackMapper.insert(new Feedback(id,phone,feedbacks,0,null));
        if(result != 1){
            log.warn("用户反馈失败");
            return "unknownWrong";
        }
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
        CommentLikes commentLikes1 = new CommentLikes(goodsId,comments,id,null);
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
            return "existWrong";
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
        //保存图片url
        User user = userMapper.selectById(id);
        if(user == null){
            log.warn("上传头像失败，用户不存在：" + id);
            return "userWrong";
        }
        if(user.getPhoto() != null){
            //删除原头像文件
            log.info("正在删除原头像文件：" + user.getPhoto());
            String lastPhotoUrl = user.getPhoto();
            File file1 = new File(lastPhotoUrl);
            if(file1.exists()){
                if(file1.delete()){
                    log.info("头像原文件删除成功");
                }
            }
        }
        user.setPhoto(filePath);
        log.info("更新头像资源路径成功：" + user.getPhoto());
        return "success";
    }

    @Override
    public String addAddress(String id, String campus, String realAddress, String name, String phone, int isDefault) {
        Address address = new Address(null,id,campus,realAddress,name,phone,null,null);
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .eq("campus",campus)
                .eq("real_address",realAddress)
                .eq("name",name)
                .eq("phone",phone);
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
            Address address1 = addressMapper.selectOne(wrapper);
            log.info("正在把其他地址设为默认");
            if(address1 != null){
                //如果还有别的地址，把其设为默认
                User user1 = new User();
                user1.setId(id);
                user1.setAddress(address1.getNumber());
                userMapper.updateById(user1);
            }else{
                //没有其他地址，告诉用户至少保留一个地址
                log.warn("没有其他地址，至少保留一个地址");
                return "addressWrong";
            }
        }
        //删除地址信息
        addressMapper.deleteById(number);
        log.info("删除地址信息成功");
        return "success";
    }

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
        jsonObject.put("shoppingList",shoppingList);
        jsonObject.put("pages",page1.getPages());
        log.info("获取购物车信息成功");
        log.info("页面数：" + page1.getPages());
        log.info("购物车信息：" + shoppingList.toString());
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
}