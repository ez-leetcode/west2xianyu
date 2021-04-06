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
@ApiModel("粉丝列表消息对象")
public class FansMsg {

    @ApiModelProperty("用户id")
    private String id;

    @ApiModelProperty("粉丝id")
    private String fansId;

    @ApiModelProperty("昵称")
    private String username;

    @ApiModelProperty("头像url")
    private String photo;

    @ApiModelProperty("自我介绍")
    private String introduction;

    @ApiModelProperty("购买商品数")
    private Integer buyCounts;

    @ApiModelProperty("关注时间")
    private Date followTime;
}
