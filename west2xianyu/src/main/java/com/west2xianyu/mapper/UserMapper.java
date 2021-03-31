package com.west2xianyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
