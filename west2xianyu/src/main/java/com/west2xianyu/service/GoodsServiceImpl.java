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
}
