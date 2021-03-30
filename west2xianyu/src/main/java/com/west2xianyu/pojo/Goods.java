package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.*;
import com.mysql.cj.conf.PropertyDefinitions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Goods {

    @TableId(type = IdType.ID_WORKER)
    private Long number;          //物品编号(类似Twitter的雪花算法，编号不唯一)

    private String fromId;        //闲置物品所有者
    private Double price;         //价格
    private String goodsName;     //物品名称
    private String description;   //作者的物品描述
    private Integer scanCounts;   //物品被浏览次数
    private Integer favorCounts;  //物品被收藏次数(取消收藏应删除)
    private String photo;         //上传闲置物品的图片url

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;      //创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;      //最近一次更新时间

    @Version
    private Integer version;      //乐观锁

    private boolean isFrozen;     //是否被冻结(有人拍下后，商品会被冻结)

    @TableLogic
    private Integer deleted;      //上传闲置物品是否符合规范，不符合则伪删除，通知用户下架(下架后才会删除)

    //用户主动删除物品时才会删除数据库中对应数据
    //物品被伪删除时，其他用户收藏夹里的商品都被标记冻结无法访问
}
