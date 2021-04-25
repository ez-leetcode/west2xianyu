package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.*;
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
@ApiModel(description = "用户地址类")
public class Address {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "用户保存地址编号")
    @TableId(type = IdType.ID_WORKER)
    private Long number;

    @ApiModelProperty(value = "用户id")
    private String id;

    @ApiModelProperty(value = "校区")
    private String campus;

    @ApiModelProperty(value = "详细地址")
    private String realAddress;

    @ApiModelProperty(value = "收货人姓名")
    private String name;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty("地址是否被删除，因为订单的地址索引，所以伪删除，保证在用户把地址删了的情况下，也能获取地址信息")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}