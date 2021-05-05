package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.*;
import com.west2xianyu.msg.*;
import com.west2xianyu.pojo.*;
import com.west2xianyu.utils.OssUtils;
import com.west2xianyu.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;


@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private FavorMapper favorMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentLikesMapper commentLikesMapper;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public GoodsMsg2 getGoods(Long number,String id) {
        Goods goods = goodsMapper.selectById(number);
        if(goods == null){
            return null;
        }
        if(goods.getIsFrozen() == 1){
            log.warn("获取商品详细信息失败，商品已被冻结");
            return null;
        }
        //商品存在且未冻结
        QueryWrapper<History> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",number)
                .eq("id",id);
        History history = historyMapper.selectOne(wrapper);
        if(history != null){
            log.info("已被浏览，正在更新");
            historyMapper.update(history,wrapper);
        }else{
            log.info("正在保存历史记录信息，商品编号：" + number + " 用户id：" + id);
            historyMapper.insert(new History(number,id,null,null));
            //把最近更新时间加上
            QueryWrapper<History> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("goods_id",number)
                    .eq("id",id);
            History history1 = historyMapper.selectOne(wrapper1);
            history1.setUpdateTime(history1.getCreateTime());
            historyMapper.update(history1,wrapper1);
        }
        //更新商品浏览信息用redis实现5分钟刷新一次
        //保存浏览信息在redis里
        redisUtils.saveScan(number.toString());
        //获取卖家信息
        User user = userMapper.selectUser(goods.getFromId());
        GoodsMsg2 goodsMsg2 = new GoodsMsg2(number,goods.getFromId(),user.getUsername(),user.getPhoto(),goods.getPrice(),goods.getPhoto(),goods.getGoodsName(),
                goods.getDescription(),goods.getScanCounts(),goods.getFavorCounts(),user.getAveDescribe(),user.getAveService(),user.getAveLogistics(),goods.getUpdateTime());
        return goodsMsg2;
    }

    @Override
    public String saveGoods(Goods goods) {
        log.info("正在上传闲置物品");
        User user = userMapper.selectById(goods.getFromId());
        if(user == null){
            log.info("上传闲置物品失败，用户不存在：" + goods.getFromId());
            return "userWrong";
        }
        goodsMapper.insert(goods);
        log.info("上传成功，物品：" + goods.toString());
        return "success";
    }

    //下架，伪删除
    @Override
    public String deleteGoods(Long number) {
        Goods goods = goodsMapper.selectById(number);
        if(goods == null){
            log.warn("下架物品失败，物品不存在：" + number);
            return "existWrong";
        }
        goodsMapper.deleteById(number);
        log.info("下架物品成功：" + number);
        //同时伪删除所有收藏夹里的相关物品
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",number);
        int result = favorMapper.delete(wrapper);
        log.info("伪删除所有收藏夹内容成功：" + result + " 条");
        return "success";
    }


    @Override
    public String changeGoods(Goods goods) {
        Goods goods1 = goodsMapper.selectById(goods.getNumber());
        if(goods1 == null || goods1.getIsFrozen() == 1){
            log.warn("修改商品信息失败，可能商品被冻结或不存在：" + goods.getNumber());
            return "existWrong";
        }
        if(goods1.getPrice() != null){
            //把原价格作为历史价格
            goods.setHisPrice(goods1.getPrice());
        }
        //重新修改商品信息要待审核
        goods.setIsFrozen(0);
        goodsMapper.updateById(goods);
        log.info("修改商品信息成功");
        return "success";
    }

    @Override
    public String addFavor(Long goodsId, String id) {
        Goods goods = goodsMapper.selectById(goodsId);
        if(goods == null){
            log.warn("添加收藏失败，商品不存在或已被冻结");
            return "existWrong";
        }
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .eq("goods_id",goodsId);
        Favor favor = favorMapper.selectOne(wrapper);
        if(favor != null){
            log.warn("添加收藏失败，商品已被收藏");
            return "repeatWrong";
        }
        Favor favor1 = favorMapper.selectByGoodsId(goodsId);
        if(favor1 != null){
            //未被明面收藏且存在，说明被逻辑删除，更新即可
            log.info("收藏对象被逻辑删除：" + favor1.toString());
            //好像逻辑删除的不能直接被更新，这里手动更新
            favorMapper.updateFavorWhenDelete(goodsId,0);
            log.info("逻辑删除更新成功");
        }else{
            //添加收藏物品
            favorMapper.insert(new Favor(goodsId,id,goods.getGoodsName(),null,null));
            log.info("添加收藏成功");
        }
        goods.setFavorCounts(goods.getFavorCounts() + 1);
        goodsMapper.updateById(goods);
        log.info("商品收藏数更新成功");
        return "success";
    }

    @Override
    public String deleteFavor(Long goodsId, String id) {
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .eq("goods_id",goodsId);
        Favor favor = favorMapper.selectOne(wrapper);
        if(favor == null){
            log.warn("移除收藏失败，商品未被收藏");
            return "existWrong";
        }
        //已添加收藏情况下，移除收藏，直接删除，不是伪删除
        favorMapper.deleteFavor(goodsId,id);
        Goods goods = goodsMapper.selectById(goodsId);
        if(goods != null){
            log.info("正在更新商品收藏数：" + goodsId);
            goods.setFavorCounts(goods.getFavorCounts() - 1);
            goodsMapper.updateById(goods);
        }
        log.info("移除收藏成功");
        return "success";
    }

    //获取未失效
    @Override
    public JSONObject getAllFavor(String id, Long cnt, Long page,String keyword) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .orderByDesc("create_time");
        if(keyword != null){
            //有关键词，加关键词条件
            wrapper.like("goods_name",keyword);
        }
        Page<Favor> page1 = new Page<>(page,cnt);
        favorMapper.selectPage(page1,wrapper);
        List<Favor> favorList = page1.getRecords();
        List<FavorMsg> favorMsgList = new LinkedList<>();
        for(Favor x: favorList){
            //获取商品实例，找到的是没有失效的，商品被下架或者deleted相关收藏也会deleted，如果原收藏商品修改后未被审核过，也可以偷偷看哦~
            Goods goods = goodsMapper.selectById(x.getGoodsId());
            favorMsgList.add(new FavorMsg(x.getGoodsId(),x.getId(),goods.getPrice(),goods.getGoodsName(),goods.getPhoto(),x.getCreateTime()));
        }
        log.info("获取收藏商品成功：" + favorMsgList.toString());
        jsonObject.put("favorList",favorMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        return jsonObject;
    }


    //获取已失效
    @Override
    public JSONObject getAllFavor1(String id, Long cnt, Long page,String keyword) {
        //mybatis-plus逻辑删除deleted查询不了已经失效的，这里手写一个并分页
        //limit起始于
        long a = cnt * (page - 1);
        //大小
        long b = cnt;
        List<Favor> favorList;
        List<Favor> favorList1;
        if(keyword != null){
            //获取该页面所有已被删除的
            favorList = favorMapper.selectFavorDeleted1(id,a,b,keyword);
            //获取所有已被删除的（为了分页）
            favorList1 = favorMapper.selectAllFavorDeleted1(id,keyword);
        }else{
            favorList = favorMapper.selectFavorDeleted(id,a,b);
            favorList1 = favorMapper.selectAllFavorDeleted(id);
        }
        //看看要不要多一页
        long i = favorList1.size() % cnt;
        long pages = favorList1.size() / cnt;
        if(i != 0){
            pages ++;
        }
        List<FavorMsg> favorMsgList = new LinkedList<>();
        for(Favor x: favorList){
            //获取商品实例，找已经被逻辑删除的
            Goods goods = goodsMapper.selectGoodsWhenDelete(x.getGoodsId());
            //商品的deleted和收藏是同步更新的，所以goods不会为null
            favorMsgList.add(new FavorMsg(x.getGoodsId(),x.getId(),goods.getPrice(),goods.getGoodsName(),goods.getPhoto(),x.getCreateTime()));
        }
        log.info("获取失效的收藏商品成功：" + favorMsgList.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("favorList",favorMsgList);
        jsonObject.put("pages",pages);
        jsonObject.put("count",favorList.size());
        return jsonObject;
    }


    //获取降价
    @Override
    public JSONObject getAllFavor2(String id, Long cnt, Long page,String keyword) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        //收藏要存在
        wrapper.eq("id",id)
                .orderByDesc("create_time");
        if(keyword != null){
            //有关键词
            wrapper.like("goods_name",keyword);
        }
        //先获取所有收藏，未被伪删除
        List<Favor> favorList = favorMapper.selectList(wrapper);
        //存全部已经降价的活着的收藏
        List<FavorMsg> favorMsgList = new LinkedList<>();
        //当前页面降价的列表
        List<FavorMsg> favorMsgList1 = new LinkedList<>();
        for(Favor x:favorList){
            Goods goods = goodsMapper.selectById(x.getGoodsId());
            if(goods.getHisPrice() != null){
                if(goods.getPrice() < goods.getHisPrice()){
                    favorMsgList.add(new FavorMsg(x.getGoodsId(),x.getId(),goods.getPrice(),goods.getGoodsName(),goods.getPhoto(),x.getCreateTime()));
                }
            }
        }
        //分页
        long sum = favorMsgList.size();
        long a = sum % cnt;
        long pages = sum / cnt;
        if(a != 0){
            pages ++;
        }
        for(long i = cnt * (page - 1); i < cnt * page && i < favorMsgList.size(); i++){
            favorMsgList1.add(favorMsgList.get((int)i));
        }
        log.info("获取降价物品成功：" + favorMsgList1.toString());
        jsonObject.put("favorList",favorMsgList1);
        jsonObject.put("pages",pages);
        jsonObject.put("count",favorMsgList.size());
        return jsonObject;
    }

    /*
    @Override
    public JSONObject searchFavor(String id, String keyword, Long cnt, Long page) {
        JSONObject jsonObject = new JSONObject();
        Page<Favor> page1 = new Page<>(page,cnt);
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .like("goods_name",keyword)
                .orderByDesc("create_time");
        favorMapper.selectPage(page1,wrapper);
        List<Favor> favorList = page1.getRecords();
        List<FavorMsg> favorMsgList = new LinkedList<>();
        for(Favor x:favorList){
            //frozen
            Goods goods = goodsMapper.selectById(x.getGoodsId());
            favorMsgList.add(new FavorMsg(goods.getNumber(),id,goods.getPrice(),goods.getGoodsName(),goods.getPhoto(),goods.getCreateTime()));
        }
        log.info("获取收藏物品成功：" + favorMsgList.toString());
        jsonObject.put("favorList",favorMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        return jsonObject;
    }

     */

    @Override
    public JSONObject searchGoods(String fromId,String keyword, Double low, Double high,Long cnt,Long page,String label1,String label2,String label3) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        if(fromId != null){
            wrapper.eq("from_id",fromId);
        }
        //未被冻结
        wrapper.eq("is_frozen",0);
        //设置价格区间
        wrapper.between("price",low,high);
        //最近更新过的物品会先被刷到
        wrapper.orderByDesc("update_time");
        if(label1 != null){
            wrapper.or()
                    .like("label1",label1)
                    .or()
                    .like("label2",label1)
                    .or()
                    .like("label3",label1);
        }
        if(label2 != null){
            wrapper.or()
                    .like("label1",label2)
                    .or()
                    .like("label2",label2)
                    .or()
                    .like("label3",label2);
        }
        if(label3 != null){
            wrapper.or()
                    .like("label1",label3)
                    .or()
                    .like("label2",label3)
                    .or()
                    .like("label3",label3);
        }
        if(keyword != null){
            //设置搜索关键词
            wrapper.or()
                    .like("goods_name",keyword);
        }
        Page<Goods> page1 = new Page<>(page,cnt);
        goodsMapper.selectPage(page1,wrapper);
        List<Goods> goodsList = page1.getRecords();
        List<GoodsMsg> goodsMsgList = new LinkedList<>();
        for(Goods x:goodsList){
            goodsMsgList.add(new GoodsMsg(x.getNumber(),x.getFromId(),x.getPrice(),x.getPhoto(),
                    x.getGoodsName(),x.getDescription(),x.getScanCounts(),x.getFavorCounts(),x.getUpdateTime()));
        }
        log.info("获取搜索商品信息成功：" + goodsMsgList.toString());
        log.info("页面数：" + page1.getPages());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goodsList",goodsMsgList);
        jsonObject.put("pages",page1.getPages());
        jsonObject.put("count",page1.getTotal());
        return jsonObject;
    }

    @Override
    public String uploadGoodsPhoto(MultipartFile file) {
        String url = OssUtils.uploadPhoto(file,"goodsPhoto");
        log.info("上传商品图片成功：" + url);
        return url;
    }


    @Override
    public JSONObject getComments(String goodsId, Long cnt, Long page) {
        Goods goods = goodsMapper.selectById(goodsId);
        if(goods == null || goods.getIsFrozen() == 1){
            //商品不存在或已失效，返回null在前台判断
            log.warn("获取评论失败，商品不存在或已失效");
            return null;
        }
        //先到这里
        Page<Comment> page1 = new Page<>(page,cnt);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id",goodsId)
                //时间倒序排列
                .orderByDesc("create_time");
        commentMapper.selectPage(page1,wrapper);
        List<Comment> commentList = page1.getRecords();
        List<CommentMsg> commentMsgList = new LinkedList<>();
        for(Comment x:commentList){
            //先查找用户昵称和头像
            User user = userMapper.selectUser(x.getId());
            //获取点赞数
            QueryWrapper<CommentLikes> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("goods_id",goodsId)
                    .eq("comments",x.getComments())
                    //用户的评论时间也要，不然有人评论两次相同的内容
                    .eq("comment_time",x.getCreateTime());
            List<CommentLikes> commentLikesList = commentLikesMapper.selectList(wrapper1);
            commentMsgList.add(new CommentMsg(x.getId(),user.getUsername(),x.getComments(),user.getPhoto(),commentLikesList.size(),x.getCreateTime()));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("commentList",commentMsgList);
        jsonObject.put("count",page1.getTotal());
        jsonObject.put("pages",page1.getPages());
        log.info("获取商品评论成功：");
        if(!commentMsgList.isEmpty()){
            log.info(commentMsgList.toString());
        }
        return jsonObject;
    }


    @Override
    public JSONObject getGoodsList(String id, Long cnt, Long page) {
        Page<Goods> page1 = new Page<>(page,cnt);
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        //获取商品信息列表
        goodsMapper.selectPage(page1,wrapper);
        List<Goods> goodsList = page1.getRecords();
        log.info("获取用户商品列表成功！");
        jsonObject.put("goodsList",goodsList);
        jsonObject.put("count",page1.getTotal());
        jsonObject.put("pages",page1.getPages());
        return jsonObject;
    }


    @Override
    public JSONObject getGoodsWhenever(Long number) {
        Goods goods = goodsMapper.selectGoodsWhenever(number);
        if(goods == null){
            log.warn("这个商品真的不存在呀！！！ 编号：" + number);
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goods",goods);
        return jsonObject;
    }

    @Override
    public JSONObject getGoodsWhenever1(Long number) {
        Goods goods = goodsMapper.selectGoodsWhenever(number);
        if(goods == null){
            log.warn("这个商品真的不存在呀！！！编号：" + number);
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        User user = userMapper.selectUser(goods.getFromId());
        GoodsMsg1 goodsMsg1 = new GoodsMsg1(goods.getNumber(),goods.getFromId(),user.getUsername(),user.getPhoto(),goods.getPrice(),
                goods.getPhoto(),goods.getGoodsName(),goods.getDescription(),goods.getScanCounts(),goods.getFavorCounts(),goods.getUpdateTime());
        jsonObject.put("goods",goodsMsg1);
        return jsonObject;
    }
}