package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(description = "关注实例类")
public class Fans {

    @ApiModelProperty(value = "被关注的id")
    private String id;

    @ApiModelProperty(value = "粉丝id")
    private String fansId;

    @ApiModelProperty(value = "关注时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
