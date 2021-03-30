package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

//被冻结物品和下架物品无法收藏
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Favor {
    private Long goodsId;       //收藏物品编号
    private String favorId;     //收藏用户

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;     //收藏时间
}
