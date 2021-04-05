package com.west2xianyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.west2xianyu.mapper.FavorMapper;
import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.mapper.HistoryMapper;
import com.west2xianyu.pojo.Favor;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.pojo.History;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
        favorMapper.insert(new Favor(goodsId,id,null));
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
}