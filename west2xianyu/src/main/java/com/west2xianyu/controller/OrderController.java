package com.west2xianyu.controller;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Order;
import com.west2xianyu.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "订单控制类",protocols = "https")
@Slf4j
@RestController
public class OrderController {


    @Autowired
    private OrderService orderService;


    @ApiOperation(value = "生成订单请求",notes = "订单生成后，状态初始为1（物品被拍下）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "toId",value = "买家id",required = true,paramType = "string")
    })
    @PostMapping("/order")
    public JSONObject generateOrder(@RequestParam("number") Long number,@RequestParam("toId") String toId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在生成订单：" + number);
        String status = orderService.generateOrder(number,toId);
        jsonObject.put("generateOrderStatus",status);
        return jsonObject;
    }


    @GetMapping("/order")
    public JSONObject getOrder(@RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在获取订单信息，订单：" + number);
        Order order = orderService.getOrder(number);
        if(order != null){
            log.warn("获取订单信息失败，订单不存在");
            jsonObject.put("getOrderStatus","existWrong");
        }
        log.info("获取订单信息成功，订单：" + order.toString());
        jsonObject.put("getOrderStatus","success");
        jsonObject.put("order",order);
        return jsonObject;
    }

}