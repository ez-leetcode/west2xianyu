package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString                   //社区管理员可以删除评论！！！！！(评论被删除通知用户)
public class Comment {

    private Long goodsId;      //被评论的物品编号
    private String fromId;     //评论用户id
    private Date time;         //评论时间
    private Integer like;      //评论被点赞数，点赞/取消点赞接口

    @TableLogic
    private Integer deleted;   //是否被删除
}
