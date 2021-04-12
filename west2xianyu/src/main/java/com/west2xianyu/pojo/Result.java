package com.west2xianyu.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//返回结果
@Setter
@Getter
@Data
public class Result<T> {

    //返回状态码
    private Integer code;

    //返回的状态消息
    private String msg;

    //返回的数据
    private T data;

}
