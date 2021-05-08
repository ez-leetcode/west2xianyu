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

@ApiModel(description = "聊天实例类")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Talk {


    @ApiModelProperty(value = "消息发送者",notes = "学号")
    private String fromId;

    @ApiModelProperty(value = "消息接受者",notes = "学号")
    private String toId;

    @ApiModelProperty(value = "消息具体内容",notes = "不超过200字")
    private String message;

    @ApiModelProperty(value = "消息接受者是否已读")
    private Integer isRead;

    @ApiModelProperty(value = "消息时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
