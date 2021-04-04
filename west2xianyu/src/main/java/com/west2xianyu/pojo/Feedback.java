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
@ApiModel("用户反馈实例")
public class Feedback {

    @ApiModelProperty(value = "反馈用户id")
    private String id;

    @ApiModelProperty(value = "联系方式")
    private String phone;

    @ApiModelProperty(value = "用户反馈内容")
    private String feedbacks;

    @ApiModelProperty(value = "是否已被管理员读过")
    private Integer isRead;

    @ApiModelProperty(value = "反馈时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
