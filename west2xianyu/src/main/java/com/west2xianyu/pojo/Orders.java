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

@ApiModel(description = "订单类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Orders {

    @ApiModelProperty(value = "订单编号",notes = "雪花算法，唯一，数据库中作为主键，和商品不是同一个编号")
    @TableId(type = IdType.ID_WORKER)
    private Long number;

    @ApiModelProperty(value = "商品编号",notes = "通过商品编号可以查看商品信息减少冗余")
    private Long goodsNumber;

    @ApiModelProperty(value = "卖家id")
    private String fromId;

    @ApiModelProperty(value = "买家id")
    private String toId;

    @ApiModelProperty(value = "物品名称",notes = "不超过20个字")
    private String goodsName;

    @ApiModelProperty(value = "物品图片url",notes = "统一存在服务器/xy/goods中")
    private String photo;

    @ApiModelProperty(value = "价格",notes = "单位(元)")
    private Double price;

    @ApiModelProperty(value = "运费",notes = "跨校区6元，不跨校区免费")
    private Double freight;

    @ApiModelProperty(value = "买家留言")
    private String message;

    @ApiModelProperty(value = "地址编号")
    private Long address;

    @ApiModelProperty(value = "当前订单状态，订单从1开始",notes = "0：未拍下 1：已拍下 2：买家已付款 3：卖家已发货 4：买家确认收货 5：订单完成")
    private Integer status;

    @ApiModelProperty(value = "拍下时间",notes = "status=0-1")
    @TableField(fill = FieldFill.INSERT)
    private Date orderTime;

    @ApiModelProperty(value = "买家支付时间",notes = "status=1-2")
    private Date payTime;

    @ApiModelProperty(value = "发货时间",notes = "status=2-3")
    private Date sendTime;

    @ApiModelProperty(value = "确认收货时间",notes = "status=3-4")
    private Date confirmTime;

    @ApiModelProperty(value = "订单完成时间",notes = "status=4-5")
    private Date finishTime;

}

/*
CREATE TABLE `west2xianyu`.`Untitled`  (
  `number` bigint(50) NOT NULL,
  `from_id` varchar(50) NULL,
  `to_id` varchar(50) NULL,
  `photo` varchar(200) NULL,
  `price` double(20, 2) NULL,
  `status` int(10) NULL,
  `order_time` datetime NULL,
  `pay_time` datetime NULL,
  `send_time` datetime NULL,
  `confirm_time` datetime NULL,
  `finish_time` datetime NULL,
  `from_evaluation` varchar(250) NULL,
  `from_evaluation_time` datetime NULL,
  `to_evaluation` varchar(250) NULL,
  `to_evaluation_time` datetime NULL,
  PRIMARY KEY (`number`)
);
 */