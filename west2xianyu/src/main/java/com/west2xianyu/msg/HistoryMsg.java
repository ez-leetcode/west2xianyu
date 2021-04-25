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
@ApiModel(description = "历史记录信息类")
public class HistoryMsg {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "闲置物品编号")
    private Long goodsId;

    @ApiModelProperty(value = "浏览用户id")
    private String id;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "价格")
    private Double price;

    @ApiModelProperty(value = "图片url")
    private String photo;

    @ApiModelProperty(value = "最近浏览时间")
    private Date updateTime;

}
