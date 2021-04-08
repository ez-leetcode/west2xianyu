package com.west2xianyu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    @Select("SELECT * FROM goods WHERE number = #{number}")
    Goods selectGoodsWhenever(Long number);

    @Select("SELECT * FROM goods WHERE number = #{number} AND deleted = 1")
    Goods selectGoodsWhenDelete(Long number);

}
