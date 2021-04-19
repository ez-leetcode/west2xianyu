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
            @ApiImplicitParam(name = "goodsName",value = "商品名",required = true,paramType = "string"),
            @ApiImplicitParam(name = "price",value = "价格",required = true,paramType = "double"),
            @ApiImplicitParam(name = "number",value = "商品编号",required = true,paramType = "long")
    })
    @ApiOperation("支付测试")
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


    @ApiOperation("回调判断是否成功付款")
    @PostMapping("/notifyBill")
    public Result<JSONObject> notifyBill(HttpServletRequest request) throws Exception{
        log.info("进来了");
        String status = alipayService.notifyPay(request);
        return ResultUtils.getResult(new JSONObject(),status);
    }




    @ApiOperation("退款商品")
    @PostMapping("/refundBill")
    public Result<JSONObject> refundBill(@RequestParam("number") Long number,
                                         @RequestParam("price") Double price) throws Exception{
        log.info("正在退款商品信息：" + number + "价格：" + price);
        String status = alipayService.refundBill(number,price);
        return ResultUtils.getResult(new JSONObject(),status);
    }


}
