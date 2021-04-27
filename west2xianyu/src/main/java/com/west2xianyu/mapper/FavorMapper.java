package com.west2xianyu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Favor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface FavorMapper extends BaseMapper<Favor> {

    //获取已经失效的收藏内容（一页）
    @Select("SELECT * FROM favor WHERE id = #{id} AND deleted = 1 ORDER BY create_time DESC LIMIT #{a} , #{b} ")
    List<Favor> selectFavorDeleted(String id,long a,long b);

    @Select("SELECT * FROM favor WHERE id = #{id} AND deleted = 1 AND goods_name LIKE #{keyword} ORDER BY create_time DESC LIMIT #{a} , #{b}")
    List<Favor> selectFavorDeleted1(String id,long a,long b,String keyword);


    //获取全部已经失效的内容
    @Select("SELECT * FROM favor WHERE id = #{id} AND deleted = 1 ORDER BY create_time DESC")
    List<Favor> selectAllFavorDeleted(String id);

    //获取全部已经失效的内容（带关键词）
    @Select("SELECT * FROM favor WHERE id = #{id} AND deleted = 1 AND goods_name LIKE #{keyword} ORDER BY create_time DESC")
    List<Favor> selectAllFavorDeleted1(String id,String keyword);


    @Select("SELECT * FROM favor WHERE goods_id = #{goods_id}")
    Favor selectByGoodsId(Long goods_id);

    @Update("UPDATE favor SET deleted = #{deleted} Where goods_id = #{goods_id}")
    void updateFavorWhenDelete(Long goods_id,int deleted);

    @Delete("DELETE FROM favor WHERE goods_id = #{goods_id} AND id = #{id}")
    void deleteFavor(Long goods_id, String id);
}
