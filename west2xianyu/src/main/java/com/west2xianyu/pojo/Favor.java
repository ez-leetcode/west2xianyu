package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "点赞实例")
public class Favor {

    @ApiModelProperty(value = "收藏物品编号",notes = "收藏时自动添加")
    private Long goodsId;

    @ApiModelProperty(value = "收藏用户")
    private String favorId;

    @ApiModelProperty(value = "收藏时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
