package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(description = "历史浏览实例")
public class History {

    @ApiModelProperty(value = "闲置物品编号")
    private Long goodsId;

    @ApiModelProperty(value = "浏览用户id")
    private String id;

    @ApiModelProperty(value = "浏览时间")
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

}
