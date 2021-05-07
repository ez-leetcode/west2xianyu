package com.west2xianyu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Comment;
import com.west2xianyu.pojo.Evaluate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT * FROM comment")
    List<Comment> selectAll();

}
