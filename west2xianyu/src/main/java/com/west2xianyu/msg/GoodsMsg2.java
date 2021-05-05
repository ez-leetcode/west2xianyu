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
@ApiModel(description = "商品展示信息类2")
public class GoodsMsg2 {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "商品编号")
    private Long number;

    @ApiModelProperty(value = "卖家id")
    private String id;

    @ApiModelProperty(value = "卖家昵称")
    private String username;

    @ApiModelProperty(value = "卖家头像url")
    private String userPhoto;

    @ApiModelProperty(value = "商品价格")
    private Double price;

    @ApiModelProperty(value = "商品图片url")
    private String photo;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "作者的物品描述",notes = "不超过250个字")
    private String description;

    @ApiModelProperty(value = "物品被浏览次数",notes = "浏览跳转时增加")
    private Integer scanCounts;

    @ApiModelProperty(value = "物品被收藏次数",notes = "取消收藏应删除，前台已被收藏不能发收藏请求")
    private Integer favorCounts;

    @ApiModelProperty(value = "卖家描述评分")
    private Double aveDescribe;

    @ApiModelProperty(value = "卖家服务评分")
    private Double aveService;

    @ApiModelProperty(value = "卖家物流评分")
    private Double aveLogistics;

    @ApiModelProperty(value = "最近上架时间")
    private Date updateTime;
}
