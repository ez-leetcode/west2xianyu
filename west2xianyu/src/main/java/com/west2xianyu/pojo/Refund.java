package com.west2xianyu.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;


@ApiModel(description = "退款管理类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Refund {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "订单编号")
    private Long number;

    @ApiModelProperty(value = "买家id")
    private String toId;

    @ApiModelProperty(value = "退款金额")
    private Double money;

    @ApiModelProperty(value = "退款原因")
    private String reason;

    @ApiModelProperty(value = "退款说明")
    private String description;

    @ApiModelProperty(value = "退款图片描述url")
    private String photo;

    @ApiModelProperty(value = "退款时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "伪删除")
    @TableLogic
    private Integer deleted;

}
