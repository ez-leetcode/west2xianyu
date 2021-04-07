package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "用户购买后评论")
public class Evaluate {

    @ApiModelProperty(value = "订单编号")
    private Long number;

    @ApiModelProperty(value = "卖家id")
    private String fromId;

    @ApiModelProperty(value = "买家id")
    private String toId;

    @ApiModelProperty(value = "评价")
    private String evaluation;

    @ApiModelProperty(value = "描述评分")
    private Double describe;

    @ApiModelProperty(value = "服务评分")
    private Double service;

    @ApiModelProperty(value = "物流评分")
    private Double logistics;

    @ApiModelProperty(value = "是否匿名",notes = "0：否 1：是")
    private Integer isNoname;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "评价时间")
    private Date createTime;

}
