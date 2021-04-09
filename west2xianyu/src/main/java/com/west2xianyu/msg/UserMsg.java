package com.west2xianyu.msg;

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
@ApiModel(description = "用户消息对象")
public class UserMsg {

    @ApiModelProperty("用户id")
    private String id;

    @ApiModelProperty("创建账号时间")
    private Date createTime;

    @ApiModelProperty("是否被冻结")
    private Integer deleted;

    @ApiModelProperty(value = "违反社区规定次数",notes = "3次将自动冻结账号30天，5次将永久封禁")
    private Integer frozenCounts;

    @ApiModelProperty("冻结时间")
    private Date frozenDate;

    @ApiModelProperty("解封时间")
    private Date reopenDate;
}
