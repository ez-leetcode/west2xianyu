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
@ApiModel(description = "商品评论列表消息对象")
public class CommentMsg {

    @ApiModelProperty("评论用户id")
    private String id;

    @ApiModelProperty("评论用户昵称")
    private String username;

    @ApiModelProperty("评论内容")
    private String comments;

    @ApiModelProperty("评论用户头像url")
    private String photo;

    @ApiModelProperty("评论点赞数")
    private Integer likes;

    @ApiModelProperty("评论时间")
    private Date create_time;

}
