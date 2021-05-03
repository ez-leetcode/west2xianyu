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
@ApiModel(description = "关注用户消息类")
public class FollowMsg {

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "用户昵称")
    private String username;

    @ApiModelProperty(value = "用户头像url")
    private String photo;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "商品编号1")
    private Long goodsId1;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "商品编号2")
    private Long goodsId2;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "商品编号3")
    private Long goodsId3;

    @ApiModelProperty(value = "商品名称1")
    private String goodsName1;

    @ApiModelProperty(value = "商品名称2")
    private String goodsName2;

    @ApiModelProperty(value = "商品名称3")
    private String goodsName3;

    @ApiModelProperty(value = "商品图片1")
    private String goodsPhoto1;

    @ApiModelProperty(value = "商品图片2")
    private String goodsPhoto2;

    @ApiModelProperty(value = "商品图片3")
    private String goodsPhoto3;

    @ApiModelProperty(value = "商品价格1")
    private Double goodsPrice1;

    @ApiModelProperty(value = "商品价格2")
    private Double goodsPrice2;

    @ApiModelProperty(value = "商品价格3")
    private Double goodsPrice3;

    @ApiModelProperty(value = "收藏时间")
    private Date createTime;
}
