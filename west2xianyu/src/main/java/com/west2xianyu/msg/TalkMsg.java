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
@ApiModel(description = "聊天消息对象")
public class TalkMsg {

    @ApiModelProperty("来自id")
    private String fromId;

    @ApiModelProperty("接收id")
    private String toId;

    @ApiModelProperty("来自用户昵称")
    private String fromUsername;

    @ApiModelProperty("接收用户昵称")
    private String toUsername;

    @ApiModelProperty("来自用户头像")
    private String fromPhoto;

    @ApiModelProperty("接收用户头像")
    private String toPhoto;

    @ApiModelProperty("信息")
    private String message;

    @ApiModelProperty("是否已读")
    private Integer isRead;

    @ApiModelProperty("发送时间")
    private Date createTime;

}
