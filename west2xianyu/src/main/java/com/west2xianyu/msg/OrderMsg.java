package com.west2xianyu.msg;


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
@ApiModel(description = "订单详细信息消息类")
public class OrderMsg {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "订单号")
    private Long number;

    @ApiModelProperty(value = "卖家id")
    private String fromId;

    @ApiModelProperty(value = "买家id")
    private String toId;

    @ApiModelProperty(value = "卖家昵称")
    private String fromName;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "价格")
    private Double price;

    @ApiModelProperty(value = "运费")
    private Double freight;

    @ApiModelProperty(value = "校区")
    private String campus;

    @ApiModelProperty(value = "真实地址")
    private String realAddress;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "收货人姓名")
    private String name;

    @ApiModelProperty(value = "商品图片url")
    private String photo;

    @ApiModelProperty(value = "订单状态")
    private Integer status;

    @ApiModelProperty(value = "订单创建时间")
    private Date createTime;
}
