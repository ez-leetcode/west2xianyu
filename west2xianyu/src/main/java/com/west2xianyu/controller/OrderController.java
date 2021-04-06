package com.west2xianyu.controller;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Orders;
import com.west2xianyu.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @ApiImplicitParam(name = "number",value = "订单编号",required = true,paramType = "long")
    @ApiOperation("获取订单详细信息")
    @GetMapping("/order")
    public JSONObject getOrder(@RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在获取订单信息，订单：" + number);
        Orders orders = orderService.getOrder(number);
        if(orders == null){
            log.warn("获取订单信息失败，订单不存在");
            jsonObject.put("getOrderStatus","existWrong");
        }else{
            log.info("获取订单信息成功，订单：" + orders.toString());
        }
        jsonObject.put("getOrderStatus","success");
        jsonObject.put("order", orders);
        return jsonObject;
    }


    //暂时不用
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "fromId",value = "卖家id",paramType = "string"),
            @ApiImplicitParam(name = "toId",value = "买家id",paramType = "string")
    })
    @ApiOperation("删除订单")
    @DeleteMapping("/order")
    public JSONObject deleteOrder(@RequestParam("number") Long number,@RequestParam(value = "fromId",required = false) String fromId,
                                  @RequestParam(value = "toId",required = false) String toId) {
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试删除订单信息：" + number);
        if((fromId == null && toId == null) || (fromId != null && toId != null)){
            log.warn("请求参数错误");
            jsonObject.put("deleteOrderStatus","requestWrong");
            return jsonObject;
        }
        String status;
        if(fromId != null){
            log.info("正在尝试删除卖家订单信息：" + fromId);
            status = orderService.deleteOrder(number,fromId,0);
        }else{
            log.info("正在尝试删除买家订单信息：" + toId);
            status = orderService.deleteOrder(number,toId,1);
        }
        jsonObject.put("deleteOrderStatus",status);
        return jsonObject;
    }
}