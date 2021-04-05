package com.west2xianyu.service;

import com.west2xianyu.pojo.Goods;

public interface GoodsService {

    Goods getGoods(Long number,String id);

    String saveGoods(Goods goods);

    String deleteGoods(Long number);

    String addFavor(Long goodsId,String id);

    String deleteFavor(Long goodsId,String id);
}
