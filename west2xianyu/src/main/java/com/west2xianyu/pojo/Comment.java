package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
@ToString                           //社区管理员可以删除评论！！！！！(评论被删除通知用户)
@ApiModel(description = "用户评论类")
public class Comment {

    @ApiModelProperty(value = "被评论的物品编号",notes = "评论时生成")
    private Long goodsId;

    @ApiModelProperty("评论用户id")
    private String fromId;

    @ApiModelProperty(value = "评论被点赞数",notes = "点赞/取消点赞接口")
    private Integer like;

    @ApiModelProperty("是否被删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("评论时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
