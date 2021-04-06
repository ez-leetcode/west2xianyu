package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}