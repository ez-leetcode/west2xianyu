package com.west2xianyu.pojo;


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
@ApiModel(description = "评论点赞实例类")
public class CommentLikes {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "闲置物品编号")
    private Long goodsId;

    @ApiModelProperty(value = "用户评论",notes = "不超过200字")
    private String comments;

    @ApiModelProperty("评论用户id")
    private String fromId;

    @ApiModelProperty("点赞用户id")
    private String likeId;

    @ApiModelProperty("用户评论的时间")
    private Date commentTime;

    @ApiModelProperty("点赞时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
