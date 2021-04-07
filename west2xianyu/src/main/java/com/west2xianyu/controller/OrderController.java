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


    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "fromId",value = "卖家id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "toId",value = "买家id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "evaluation",value = "评价",required = true,paramType = "string"),
            @ApiImplicitParam(name = "describe",value = "描述评价",required = true,paramType = "double"),
            @ApiImplicitParam(name = "service",value = "服务评价",required = true,paramType = "double"),
            @ApiImplicitParam(name = "logistics",value = "快递评价",required = true,paramType = "double"),
            @ApiImplicitParam(name = "isNoname",value = "是否匿名",required = true,paramType = "int")
    })
    @ApiOperation("购买后评价商品")
    @PostMapping("/evaluate")
    public JSONObject evaluateOrder(@RequestParam("number") Long number,@RequestParam("fromId") String fromId,
                                    @RequestParam("toId") String toId,@RequestParam("describe") Double describe,
                                    @RequestParam("service") Double service,@RequestParam("logistics") Double logistics,
                                    @RequestParam("isNoname") int isNoname,@RequestParam("evaluation") String evaluation){
        JSONObject jsonObject = new JSONObject();
        log.info("正在添加商品评价：" + describe);
        String status = orderService.evaluateOrder(number,fromId,toId,describe,service,logistics,isNoname,evaluation);
        jsonObject.put("evaluateOrderStatus",status);
        return jsonObject;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "status",value = "订单状态",required = true,paramType = "int"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long")
    })
    @ApiOperation("获取所有订单页面")
    @GetMapping("/orderList")
    public JSONObject getOrderList(@RequestParam("id") String id,@RequestParam("status") int status,
                                   @RequestParam(value = "keyword",required = false) String keyword, @RequestParam("cnt") long cnt,
                                   @RequestParam("page") long page){
        log.info("正在获取所有订单页面，id：" + id + " status：" + status);
        JSONObject jsonObject = orderService.getOrderList(id,keyword,status,cnt,page);
        log.info("获取所有订单页面成功");
        return jsonObject;
    }

}