package com.west2xianyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Refund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RefundMapper extends BaseMapper<Refund> {


    @Update("UPDATE refund SET deleted = 1 WHERE number = #{number}")
    void deletedRefund(Long number);

}
