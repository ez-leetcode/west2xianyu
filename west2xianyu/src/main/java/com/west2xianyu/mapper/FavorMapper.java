package com.west2xianyu.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.west2xianyu.pojo.Favor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FavorMapper extends BaseMapper<Favor> {

    //获取已经失效的收藏内容（一页）
    @Select("SELECT * FROM favor WHERE id = #{id} AND deleted = 1 ORDER BY create_time DESC LIMIT #{a} , #{b} ")
    List<Favor> selectFavorDeleted(String id,long a,long b);

    //获取全部已经失效的内容
    @Select("SELECT * FROM favor WHERE id = #{id} AND deleted = 1 ORDER BY create_time DESC")
    List<Favor> selectAllFavorDeleted(String id);


    @Select("SELECT * FROM favor WHERE goods_id = #{goods_id}")
    Favor selectByGoodsId(Long goods_id);

    @Update("UPDATE favor SET deleted = #{deleted} Where goods_id = #{goods_id}")
    void updateFavorWhenDelete(Long goods_id,int deleted);
}
