package com.west2xianyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Select("SELECT * FROM user WHERE id LIKE #{keyword} ORDER BY frozen_counts DESC LIMIT #{a},#{b}")
    List<User> getAllUser(String keyword,long a,long b);

    @Select("SELECT * FROM user ORDER BY frozen_counts DESC LIMIT #{a},#{b}")
    List<User> getAllUer1(long a,long b);

    //用于分页
    @Select("SELECT * FROM user")
    List<User> selectAll();

    //用于分页
    @Select("SELECT * FROM user WHERE id LIKE #{keyword}")
    List<User> selectAll1(String keyword);

    //按解封时间排序
    //用于分页
    @Select("SELECT * FROM user WHERE id LIKE #{keyword} AND deleted = 1 ORDER BY reopen_date DESC")
    List<User> selectDeletedUser(String keyword);

    //用于分页
    @Select("SELECT * FROM user WHERE deleted = 1 ORDER BY reopen_date")
    List<User> selectDeletedUser1();

    @Select("SELECT * FROM user WHERE id LIKE #{keyword} AND deleted = 1 ORDER BY reopen_date DESC LIMIT #{a} , #{b}")
    List<User> selectDeletedUser2(String keyword,long a,long b);

    @Select("SELECT * FROM user WHERE deleted = 1 ORDER BY reopen_date DESC LIMIT #{a} , #{b}")
    List<User> selectDeletedUser3(long a,long b);

    @Select("SELECT * FROM user WHERE id = #{id} AND deleted = 1")
    User selectUserWhenever(String id);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectUser(String id);
}
