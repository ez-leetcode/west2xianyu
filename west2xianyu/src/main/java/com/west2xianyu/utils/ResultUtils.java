package com.west2xianyu.utils;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Result;

import java.util.HashMap;

//返回结果工具类
public class ResultUtils {

    private static final HashMap<String,Integer> resultMap = new HashMap<>();


    static{
        resultMap.put("success",200);
        resultMap.put("userWrong",1);
        resultMap.put("statusWrong",2);
        resultMap.put("repeatWrong",3);
    }


    //object是json数据，msg是状态
    public static Result<JSONObject> getResult(JSONObject object, String msg){
        Result<JSONObject> result = new Result<>();
        result.setCode(resultMap.get(msg));
        result.setMsg(msg);
        result.setData(object);
        return result;
    }

}
