package com.west2xianyu.msg;

import io.swagger.annotations.Api;
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
@ApiModel("商品展示信息类")
public class GoodsMsg {

    @ApiModelProperty(value = "商品编号")
    private Long number;

    @ApiModelProperty(value = "卖家id")
    private String id;

    @ApiModelProperty(value = "商品价格")
    private Double price;

    @ApiModelProperty(value = "商品图片url")
    private String photo;

    @ApiModelProperty(value = "作者的物品描述",notes = "不超过250个字")
    private String description;

    @ApiModelProperty(value = "最近上架时间")
    private Date updateTime;

}
