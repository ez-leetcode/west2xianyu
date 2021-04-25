package com.west2xianyu.msg;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(description = "商家商品评价消息类")
public class EvaluateMsg {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "订单编号")
    private Long number;

    @ApiModelProperty(value = "卖家id")
    private String fromId;

    @ApiModelProperty(value = "买家id")
    private String toId;

    @ApiModelProperty(value = "买家昵称")
    private String username;

    @ApiModelProperty(value = "用户头像url")
    private String userPhoto;

    @ApiModelProperty(value = "评价图片url")
    private String evaluatePhoto;

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

    @ApiModelProperty(value = "评价时间")
    private Date createTime;

}
