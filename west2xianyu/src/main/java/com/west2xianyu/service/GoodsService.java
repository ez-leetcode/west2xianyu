package com.west2xianyu.service;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Goods;

public interface GoodsService {

    Goods getGoods(Long number,String id);

    String saveGoods(Goods goods);

    String deleteGoods(Long number);

    String addFavor(Long goodsId,String id);

    String deleteFavor(Long goodsId,String id);

    JSONObject getAllFavor(String id,Long cnt,Long page);

    JSONObject searchFavor(String id,String keyword,Long cnt,Long page);

    JSONObject searchGoods(String keyword,Double low,Double high,Long cnt,Long page);
}
