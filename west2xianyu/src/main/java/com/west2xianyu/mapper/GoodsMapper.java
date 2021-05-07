package com.west2xianyu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    @Select("SELECT * FROM goods WHERE number = #{number}")
    Goods selectGoodsWhenever(Long number);

    @Select("SELECT * FROM goods WHERE number = #{number} AND deleted = 1")
    Goods selectGoodsWhenDelete(Long number);

    @Update("UPDATE goods SET deleted = 0 WHERE number = #{number}")
    void reopenGoods(Long number);

    @Select("SELECT * FROM goods")
    List<Goods> selectAll();
}
