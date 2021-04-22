package com.west2xianyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface AddressMapper extends BaseMapper<Address> {

    //获取所有情况下的地址信息
    @Select("SELECT * FROM address WHERE number = #{number}")
    Address getAddress(Long number);

}
