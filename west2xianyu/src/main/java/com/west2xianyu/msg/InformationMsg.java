package com.west2xianyu.msg;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(description = "后台主页信息类")
public class InformationMsg {

    @ApiModelProperty(value = "用户总数（不包括被封号的）")
    private Integer userCount;

    @ApiModelProperty(value = "订单总数")
    private Integer orderCount;

    @ApiModelProperty(value = "交易金额")
    private Double tradeCount;

    @ApiModelProperty(value = "待评价订单数")
    private Integer orderCount1;

    @ApiModelProperty(value = "待发货订单数")
    private Integer orderCount2;

    @ApiModelProperty(value = "待付款订单数")
    private Integer orderCount3;

    @ApiModelProperty(value = "已成交订单数")
    private Integer orderCount4;

    @ApiModelProperty(value = "交易失败订单数")
    private Integer orderCount5;

    @ApiModelProperty(value = "商品总数")
    private Integer goodsCount;

    @ApiModelProperty(value = "上架商品总数")
    private Integer goodsCount1;

    @ApiModelProperty(value = "下架商品总数")
    private Integer goodsCount2;

    @ApiModelProperty(value = "商品评论总数（商家下面那个）")
    private Integer evaluate;

    @ApiModelProperty(value = "商品留言总数")
    private Integer comments;

    @ApiModelProperty(value = "反馈信息1")
    private String feedback1;

    @ApiModelProperty(value = "反馈信息2")
    private String feedback2;

    @ApiModelProperty(value = "反馈信息3")
    private String feedback3;

    @ApiModelProperty(value = "反馈信息4")
    private String feedback4;

    @ApiModelProperty(value = "反馈信息5")
    private String feedback5;
}
