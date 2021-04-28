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
@ApiModel(description = "用户举报实例类")
public class Complain {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "举报编号",notes = "雪花算法，唯一，数据库中作为主键，和商品不是同一个编号")
    @TableId(type = IdType.ID_WORKER)
    private Long number;

    @ApiModelProperty(value = "投诉者id")
    private String fromId;

    @ApiModelProperty(value = "被投诉者id")
    private String toId;

    @ApiModelProperty(value = "投诉理由")
    private String reason;

    @ApiModelProperty(value = "具体原因")
    private String specificReason;

    @ApiModelProperty(value = "伪删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("用户举报创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("最近一次更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
