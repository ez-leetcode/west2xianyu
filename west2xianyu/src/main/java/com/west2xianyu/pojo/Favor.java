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
@ApiModel(description = "收藏物品实例")
public class Favor {

    @ApiModelProperty(value = "收藏物品编号",notes = "收藏时自动添加")
    private Long goodsId;

    @ApiModelProperty(value = "收藏用户")
    private String id;

    @ApiModelProperty(value = "收藏物品名称",notes = "虽然冗余，但是减少sql次数")
    private String goodsName;

    @ApiModelProperty(value = "收藏时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}