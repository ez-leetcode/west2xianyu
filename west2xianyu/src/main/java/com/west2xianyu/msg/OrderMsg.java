package com.west2xianyu.msg;


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
@ApiModel(description = "订单消息类")
public class OrderMsg {

    @ApiModelProperty(value = "订单号")
    private Long number;

    @ApiModelProperty(value = "卖家id")
    private String fromId;

    @ApiModelProperty(value = "卖家昵称")
    private String fromName;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "价格")
    private Double price;

    @ApiModelProperty(value = "运费")
    private Double freight;

    @ApiModelProperty(value = "商品图片url")
    private String photo;

    @ApiModelProperty(value = "订单创建时间")
    private Date createTime;
}
