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

@ApiModel("订单类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString        //管理员可以删除订单,取消订单，删除订单
public class Order {

    @ApiModelProperty(value = "订单编号",notes = "雪花算法，唯一，数据库中作为主键")
    @TableId(type = IdType.ID_WORKER)
    private Long number;

    //3.30暂时到这


    private String fromId;            //发货方(发货方信息可以由id查出，减少冗余)
    private String toId;              //接收方
    private Double price;             //价格
    private Integer status;           //当前订单状态

    @TableField(fill = FieldFill.INSERT)
    private Date orderTime;           //订单开始时间

    private Date finishTime;          //订单完成时间
    private String fromEvaluation;    //发货方交易评价
    private Date fromEvaluationTime;  //发货方交易评价时间
    private String toEvaluation;      //收货方交易评价
    private Date toEvaluationTime;    //收货方交易评价时间
}
