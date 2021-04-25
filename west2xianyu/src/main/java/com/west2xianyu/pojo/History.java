package com.west2xianyu.pojo;


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
@ApiModel(description = "历史浏览实例")
public class History {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "闲置物品编号")
    private Long goodsId;

    @ApiModelProperty(value = "浏览用户id")
    private String id;

    @ApiModelProperty(value = "第一次浏览时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "最近浏览时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}
