package com.west2xianyu.service;

import com.west2xianyu.pojo.Goods;

public interface GoodsService {

    Goods getGoods(Long number);

    String saveGoods(Goods goods);

}
