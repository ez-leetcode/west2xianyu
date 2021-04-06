package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(description = "用户类")
public class User {

    @ApiModelProperty(value = "用户唯一id(学号)",notes = "主键")
    private String id;

    @ApiModelProperty(value = "用户昵称",notes = "(可以更改，不唯一)")
    private String username;

    @ApiModelProperty(value = "密码",notes = "密码由前台MD5(salt)加密后传输")
    private String password;

    @ApiModelProperty(value = "性别",notes = "M/W")
    private String sex;

    @ApiModelProperty(value = "收货地址编号")
    private Long address;

    @ApiModelProperty(value = "邮箱",notes = "用户没设置，注册时默认用学号生成学校邮箱")
    private String email;

    @ApiModelProperty(value = "校区")
    private String campus;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty(value = "头像url",notes = "头像资源统一存放在服务器路径：/xy/photo中")
    private String photo;

    @ApiModelProperty(value = "自我介绍",notes = "在查看个人信息和粉丝列表的粉丝简介中呈现")
    private String introduction;

    @ApiModelProperty(value = "粉丝数",notes = "在查看个人信息中呈现(粉丝列表功能)")
    private Integer fansCounts;

    @ApiModelProperty(value = "关注数",notes = "在个人信息中呈现(关注列表功能)")
    private Integer followCounts;

    @ApiModelProperty(value = "违反社区规定次数",notes = "3次将自动冻结账号30天，5次将永久封禁")
    private Integer frozenCounts;

    @ApiModelProperty(value = "售卖单量")
    private Integer saleCounts;

    @ApiModelProperty(value = "描述评分",notes = "默认为5分")
    private Double aveDescribe;

    @ApiModelProperty(value = "服务评分")
    private Double aveService;

    @ApiModelProperty(value = "物流评分")
    private Double aveLogistics;

    @ApiModelProperty("是否为管理员")
    private Integer isAdministrator;

    @ApiModelProperty("是否账号被冻结")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("账号创建日期")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("最近更新日期")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty("冻结时间")
    private Date frozenDate;

    @ApiModelProperty("解封时间")
    private Date reopenDate;

}

/*
    CREATE TABLE `west2xianyu`.`Untitled`  (
        `id` bigint(50) NOT NULL,
        `username` varchar(50) NULL,
        `password` varchar(50) NULL,
        `sex` varchar(10) NULL,
        `address` varchar(150) NULL,
        `email` varchar(50) NULL,
        `phone` varchar(50) NULL,
        `photo` varchar(150) NULL,
        `introduction` varchar(200) NULL,
        `fansCounts` int(20) NULL,
        `frozenCounts` int(10) NULL,
        `isAdministrator` int(10) NULL,
        `deleted` int(10) NULL,
        `createTime` datetime NULL,
        `updateTime` datetime NULL,
        `frozenDate` datetime NULL,
        `reopenDate` datetime NULL,
        PRIMARY KEY (`id`)
        );
 */

//    git ls-files | xargs wc -l