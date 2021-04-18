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
        resultMap.put("yzmWrong",401);
        resultMap.put("existWrong",1);
        resultMap.put("authorityWrong",403);
        resultMap.put("logoutSuccess",200);
        resultMap.put("loginSuccess",200);
        resultMap.put("paySuccess",200);
        resultMap.put("tokenWrong",403);
        resultMap.put("payFail",-1);
        resultMap.put("refundFail",-1);
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
