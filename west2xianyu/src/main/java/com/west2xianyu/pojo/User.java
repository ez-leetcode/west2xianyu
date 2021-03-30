package com.west2xianyu.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private Integer id;              //用户唯一(学号)
    private String username;         //用户昵称(可以更改，不唯一)
    private String password;         //密码
    private String sex;              //性别
    private String address;          //收货地址
    private String email;            //邮箱
    private String phone;            //手机号码
    private String photo;            //头像url
    private String introduction;     //自我介绍
    private Integer fansCounts;      //粉丝数 -------1.查看粉丝列表
    private Integer frozenCounts;    //违反社区规定次数(3次将自动冻结账号30天，5次将永久封禁)
    private boolean isAdministrator; //是否为管理员

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;         //账号创建日期

    @TableLogic
    private Integer deleted;         //是否账号被冻结

    private Date frozenDate;         //冻结时间
    private Date reopenedDate;       //解封时间





    //加上更新时间
}
