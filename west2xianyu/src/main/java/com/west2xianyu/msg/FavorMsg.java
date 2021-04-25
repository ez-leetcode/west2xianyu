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
@ApiModel(description = "收藏商品消息对象")
public class FavorMsg {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "收藏物品编号")
    private Long goodsId;

    @ApiModelProperty(value = "收藏用户")
    private String id;

    @ApiModelProperty(value = "价格")
    private Double price;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品图片路径")
    private String photo;

    @ApiModelProperty(value = "收藏时间")
    private Date createTime;

}
