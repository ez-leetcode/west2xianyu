package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Result;
import com.west2xianyu.service.AlipayService;
import com.west2xianyu.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Api(tags = "支付宝控制类")
@RestController
public class AlipayController {


    @Autowired
    private AlipayService alipayService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsName",value = "商品名",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "price",value = "价格",required = true,dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "long",paramType = "query")
    })
    @ApiOperation(value = "支付订单",notes = "请求会返回一段js代码可以自动跳转到支付宝支付页面")
    @GetMapping("/payBill")
    public void payBill(HttpServletResponse response, HttpServletRequest request,
                           @RequestParam("goodsName") String goodsName,
                           @RequestParam("price") Double price,
                           @RequestParam("number") Long number){

        try {
            alipayService.aliPay(response,request,goodsName,price,number);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "回调判断是否成功付款",notes = "这个接口付完款，支付宝会调用回调，自动改变订单状态，不用你调用哈~")
    @PostMapping("/notifyBill")
    public Result<JSONObject> notifyBill(HttpServletRequest request) throws Exception{
        log.info("正在回调付款信息");
        String status = alipayService.notifyPay(request);
        return ResultUtils.getResult(new JSONObject(),status);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "price",value = "price",required = true,dataType = "Double",paramType = "query")
    })
    @ApiOperation(value = "退款商品",notes = "existWrong：订单不存在 statusWrong：订单状态有误 refundWrong：系统退款错误（一般不会有） success：成功")
    @PostMapping("/refundBill")
    public Result<JSONObject> refundBill(@RequestParam("number") Long number,
                                         @RequestParam("price") Double price) throws Exception{
        log.info("正在退款商品信息：" + number + "价格：" + price);
        String status = alipayService.refundBill(number,price);
        return ResultUtils.getResult(new JSONObject(),status);
    }


}