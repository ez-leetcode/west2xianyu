package com.west2xianyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Evaluate;
import com.west2xianyu.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EvaluateMapper extends BaseMapper<Evaluate> {

    @Select("SELECT * FROM evaluate")
    List<Evaluate> selectAll();

}
