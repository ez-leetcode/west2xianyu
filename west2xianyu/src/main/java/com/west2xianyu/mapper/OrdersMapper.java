package com.west2xianyu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {


    @Select("SELECT * FROM orders")
    List<Orders> selectAll();

}
