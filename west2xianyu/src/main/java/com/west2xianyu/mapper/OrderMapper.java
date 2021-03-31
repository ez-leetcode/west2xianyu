package com.west2xianyu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
