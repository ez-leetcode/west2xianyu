package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.*;
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
@ApiModel("闲置物品类")
public class Goods {

    @ApiModelProperty(value = "物品编号",notes = "(类似Twitter的雪花算法，编号不唯一)")
    @TableId(type = IdType.ID_WORKER)
    private Long number;

    @ApiModelProperty(value = "闲置物品所有者id")
    private String fromId;

    @ApiModelProperty(value = "商品价格",notes = "单位(元)")
    private Double price;

    @ApiModelProperty(value = "历史价格",notes = "前台可选择显示历史价格")
    private Double hisPrice;

    @ApiModelProperty(value = "物品名称",notes = "不超过20个字")
    private String goodsName;

    @ApiModelProperty(value = "作者的物品描述",notes = "不超过250个字")
    private String description;

    @ApiModelProperty(value = "物品被浏览次数",notes = "浏览跳转时增加")
    private Integer scanCounts;

    @ApiModelProperty(value = "物品被收藏次数",notes = "取消收藏应删除，前台已被收藏不能发收藏请求")
    private Integer favorCounts;

    @ApiModelProperty(value = "闲置物品图片url",notes = "闲置物品url统一存在/xy/goods")
    private String photo;

    @ApiModelProperty(value = "物品标签1")
    private String label1;

    @ApiModelProperty(value = "物品标签2")
    private String label2;

    @ApiModelProperty(value = "物品标签3")
    private String label3;


    @ApiModelProperty("闲置物品创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("最近一次更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("乐观锁")
    @Version
    private Integer version;

    @ApiModelProperty(value = "是否被冻结",notes = "有人拍下后，商品会被冻结，不能被搜索到")
    private Integer isFrozen;

    @ApiModelProperty(value = "伪删除",notes = "上传闲置物品是否符合规范，不符合则伪删除，通知用户下架(下架后才会删除)")
    @TableLogic
    private Integer deleted;

    //用户主动删除物品时才会删除数据库中对应数据
    //物品被伪删除时，其他用户收藏夹里的商品都被标记冻结无法访问
}
