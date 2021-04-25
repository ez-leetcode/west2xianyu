package com.west2xianyu.msg;


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
@ApiModel(description = "用户反馈消息类")
public class FeedbackMsg {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "反馈内容编号")
    private Long number;

    @ApiModelProperty(value = "反馈用户id")
    private String id;

    @ApiModelProperty(value = "用户昵称")
    private String username;

    @ApiModelProperty(value = "头像url")
    private String photo;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "反馈内容")
    private String feedbacks;

    @ApiModelProperty(value = "是否已被管理员读过")
    private Integer isRead;

    @ApiModelProperty(value = "反馈时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}