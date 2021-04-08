package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.west2xianyu.mapper.FavorMapper;
import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.mapper.HistoryMapper;
import com.west2xianyu.msg.FavorMsg;
import com.west2xianyu.msg.GoodsMsg;
import com.west2xianyu.pojo.Favor;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.pojo.History;
import com.west2xianyu.utils.OssUtils;
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


    @Override
    public Goods getGoods(Long number,String id) {
        Goods goods = goodsMapper.selectById(number);
        if(goods == null){
            return null;
        }
        if(goods.getIsFrozen() == 1){
            return goods;
        }
        //商品存在且未冻结
        QueryWrapper<History> wrapper = new QueryWrapper<>();
        wrapper.eq("number",number)
                .eq("id",id);
        History history = historyMapper.selectOne(wrapper);
        if(history != null){
            log.info("已被浏览，正在更新");
            historyMapper.updateById(history);
        }
        log.info("正在保存历史记录信息，商品编号：" + number + "用户id：" + id);
        historyMapper.insert(new History(number,id,null,null));
        log.info("正在更新商品浏览信息");
        goods.setScanCounts(goods.getScanCounts() + 1);
        goodsMapper.updateById(goods);
        return goods;
    }

    @Override
    public String saveGoods(Goods goods) {
        log.info("正在上传闲置物品");
        goodsMapper.insert(goods);
        log.info("上传成功，物品：" + goods.toString());
        return "success";
    }

    @Override
    public String deleteGoods(Long number) {
        Goods goods = goodsMapper.selectById(number);
        if(goods == null){
            log.warn("下架物品失败，物品不存在：" + number);
            return "existWrong";
        }
        goodsMapper.deleteById(number);
        log.info("下架物品成功：" + number);
        return "success";
    }


    @Override
    public String changeGoods(Goods goods) {
        Goods goods1 = goodsMapper.selectById(goods.getNumber());
        if(goods1 == null){
            log.warn("修改商品信息失败，可能商品被冻结或不存在：" + goods.getNumber());
            return "existWrong";
        }
        if(goods1.getPrice() != null){
            //把原价格作为历史价格
            goods.setHisPrice(goods1.getPrice());
        }
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
        //添加收藏物品
        favorMapper.insert(new Favor(goodsId,id,goods.getGoodsName(),null));
        log.info("添加收藏成功");
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
        //已添加收藏情况下，移除收藏
        favorMapper.delete(wrapper);
        Goods goods = goodsMapper.selectById(goodsId);
        if(goods != null){
            log.info("正在更新商品收藏数：" + goodsId);
            goods.setFavorCounts(goods.getFavorCounts() - 1);
            goodsMapper.updateById(goods);
        }
        log.info("移除收藏成功");
        return "success";
    }

    @Override
    public JSONObject getAllFavor(String id, Long cnt, Long page) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .orderByDesc("create_time");
        Page<Favor> page1 = new Page<>(page,cnt);
        favorMapper.selectPage(page1,wrapper);
        List<Favor> favorList = page1.getRecords();
        List<FavorMsg> favorMsgList = new LinkedList<>();
        for(Favor x: favorList){
            //获取商品实例，找不管有没有被逻辑删除的
            Goods goods = goodsMapper.selectGoodsWhenever(x.getGoodsId());
            favorMsgList.add(new FavorMsg(x.getGoodsId(),x.getId(),goods.getPrice(),goods.getGoodsName(),goods.getPhoto(),x.getCreateTime()));
        }
        log.info("获取收藏商品成功：" + favorMsgList.toString());
        jsonObject.put("favorList",favorMsgList);
        jsonObject.put("pages",page1.getPages());
        return jsonObject;
    }


    //待完成
    @Override
    public JSONObject getAllFavor1(String id, Long cnt, Long page) {
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .orderByDesc("create_time");
        Page<Favor> page1 = new Page<>(page,cnt);
        favorMapper.selectPage(page1,wrapper);
        List<FavorMsg> favorMsgList = new LinkedList<>();
        List<Favor> favorList = page1.getRecords();
        for(Favor x: favorList){
            //获取商品实例，找已经被逻辑删除的
            Goods goods = goodsMapper.selectGoodsWhenDelete(x.getGoodsId());
            if(goods != null){
                favorMsgList.add(new FavorMsg(x.getGoodsId(),x.getId(),goods.getPrice(),goods.getGoodsName(),goods.getPhoto(),x.getCreateTime()));
            }
        }
        log.info("获取失效的收藏商品成功：" + favorMsgList.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("favorList",favorMsgList);
        jsonObject.put("pages",page1.getPages());
        return jsonObject;
    }


    //待完成
    @Override
    public JSONObject getAllFavor2(String id, Long cnt, Long page) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<Favor> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id)
                .orderByDesc("create_time");
        return jsonObject;
    }

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
        return jsonObject;
    }

    @Override
    public JSONObject searchGoods(String keyword, Double low, Double high,Long cnt,Long page,String label1,String label2,String label3) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
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
                    x.getGoodsName(),x.getDescription(),x.getUpdateTime()));
        }
        log.info("获取搜索商品信息成功：" + goodsMsgList.toString());
        log.info("页面数：" + page1.getPages());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goodsList",goodsMsgList);
        jsonObject.put("pages",page1.getPages());
        return jsonObject;
    }

    @Override
    public String uploadGoodsPhoto(MultipartFile file) {
        String url = OssUtils.uploadPhoto(file,"goodsPhoto");
        log.info("上传商品图片成功：" + url);
        return url;
    }
}