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

@ApiModel(description = "购物车实例类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Shopping {

    @ApiModelProperty(value = "闲置物品编号",notes = "和闲置物品编号一致，不是订单编号")
    private Long number;

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "放入购物车时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
