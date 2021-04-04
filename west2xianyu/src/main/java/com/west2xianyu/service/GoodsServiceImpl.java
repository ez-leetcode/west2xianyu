package com.west2xianyu.service;

import com.west2xianyu.mapper.GoodsMapper;
import com.west2xianyu.pojo.Goods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Goods getGoods(Long number) {
        return goodsMapper.selectById(number);
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
}