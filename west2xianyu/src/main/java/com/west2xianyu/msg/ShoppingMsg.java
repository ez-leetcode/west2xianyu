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
@ApiModel("购物车列表消息对象")
public class ShoppingMsg {

    @ApiModelProperty("商品编号")
    private Long number;

    @ApiModelProperty("卖家id")
    private String formId;

    @ApiModelProperty("价格")
    private Double price;

    @ApiModelProperty("运费")
    private Double freight;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品描述")
    private String description;

    @ApiModelProperty("商品图片url")
    private String photo;

    @ApiModelProperty("放入购物车时间")
    private Date creatTime;

}
