package com.west2xianyu.controller;

import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.msg.OrderMsg;
import com.west2xianyu.pojo.Result;
import com.west2xianyu.service.AlipayService;
import com.west2xianyu.service.OrderService;
import com.west2xianyu.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



//   0.订单被取消  1.订单已拍下 2.买家已付款 3.卖家已发货 4.买家确认收货==订单已完成（需评价） 5.买家已评价  8.订单已全部完成   10.申请退款  11.退款成功  12.退款失败

@Api(tags = "订单控制类",protocols = "https")
@Slf4j
 @RestController
public class OrderController {


    @Autowired
    private OrderService orderService;

    /*
    @Autowired
    private AlipayService alipayService;

     */

    @ApiOperation(value = "生成订单请求",notes = "repeatWrong：该商品已被下单 existWrong：该商品部存在 frozenWrong：该商品已被冻结 addressWrong：地址编号对应地址不存在 success：成功")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "toId",value = "买家id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "address",value = "地址编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "message",value = "卖家留言",dataType = "string",paramType = "query"),
    })
    @PostMapping("/order")
    public Result<JSONObject> generateOrder(@RequestParam("number") Long number, @RequestParam("toId") String toId,
                                            @RequestParam(value = "message",required = false) String message,
                                            @RequestParam("address") Long address){
        JSONObject jsonObject = new JSONObject();
        log.info("正在生成订单：" + number);
        String status = orderService.generateOrder(number,toId,message,address);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    //应该校验买卖家，不能让别人看到
    @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query")
    @ApiOperation(value = "获取订单详细信息",notes = "existWrong：订单不存在 success：成功  成功返回json order：订单具体内容")
    @GetMapping("/order")
    public Result<JSONObject> getOrder(@RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在获取订单信息，订单：" + number);
        OrderMsg orders = orderService.getOrder(number);
        String status;
        if(orders == null){
            log.warn("获取订单信息失败，订单不存在");
            status = "existWrong";
        }else{
            log.info("获取订单信息成功，订单：" + orders.toString());
            status = "success";
        }
        jsonObject.put("order", orders);
        return ResultUtils.getResult(jsonObject,status);
    }


    //暂时不用
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "fromId",value = "卖家id",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "toId",value = "买家id",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "删除订单",notes = "existWrong：订单已被删除（可能是重复请求） success：成功")
    @DeleteMapping("/order")
    public Result<JSONObject> deleteOrder(@RequestParam("number") Long number,@RequestParam(value = "fromId",required = false) String fromId,
                                          @RequestParam(value = "toId",required = false) String toId) {
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试删除订单信息：" + number);
        if((fromId == null && toId == null) || (fromId != null && toId != null)){
            log.warn("请求参数错误");
            return ResultUtils.getResult(jsonObject,"requestWrong");
        }
        String status;
        if(fromId != null){
            log.info("正在尝试删除卖家订单信息：" + fromId);
            status = orderService.deleteOrder(number,fromId,0);
        }else{
            log.info("正在尝试删除买家订单信息：" + toId);
            status = orderService.deleteOrder(number,toId,1);
        }
        return ResultUtils.getResult(jsonObject,status);
    }


    //待评价
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "fromId",value = "卖家id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "toId",value = "买家id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "evaluation",value = "评价",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "describe",value = "描述评价",required = true,dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "service",value = "服务评价",required = true,dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "logistics",value = "快递评价",required = true,dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "isNoname",value = "是否匿名",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "photo",value = "描述的图片",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "购买后评价商品",notes = "existWrong：订单不存在或已被冻结 statusWrong：订单状态有误 userWrong：卖家已被封号 success：成功")
    @PostMapping("/evaluate")
    public Result<JSONObject> evaluateOrder(@RequestParam("number") Long number,@RequestParam("fromId") String fromId,
                                    @RequestParam("toId") String toId,@RequestParam("describe") Double describe,
                                    @RequestParam("service") Double service,@RequestParam("logistics") Double logistics,
                                    @RequestParam("isNoname") int isNoname,@RequestParam("evaluation") String evaluation,
                                            @RequestParam(value = "photo",required = false) String photo){
        JSONObject jsonObject = new JSONObject();
        log.info("正在添加商品评价：" + describe);
        String status = orderService.evaluateOrder(number,fromId,toId,describe,service,logistics,isNoname,evaluation,photo);
        return ResultUtils.getResult(jsonObject,status);
    }

    @ApiImplicitParam(name = "photo",value = "描述图片文件",required = true,dataType = "file",paramType = "body")
    @ApiOperation(value = "上传评价描述图片",notes = "fileWrong：上传文件为空 typeWrong：文件类型有误 success：成功 成功返回json url：图片url")
    @PostMapping("/evaluatePhoto")
    public Result<JSONObject> evaluatePhotoUpload(@RequestParam("photo") MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        log.info("正在上传评价描述图片");
        String status = orderService.evaluatePhotoUpload(file);
        if(status.length() > 12){
            //返回的是url
            jsonObject.put("url",status);
            return ResultUtils.getResult(jsonObject,"success");
        }
        return ResultUtils.getResult(jsonObject,status);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "status",value = "订单状态,-1代表全部状态",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取所有订单页面",notes = "success：成功 成功返回json orderList：订单列表 pages：页面数 count：总数")
    @GetMapping("/orderList")
    public Result<JSONObject> getOrderList(@RequestParam("id") String id,@RequestParam("status") int status,
                                   @RequestParam(value = "keyword",required = false) String keyword, @RequestParam("cnt") long cnt,
                                   @RequestParam("page") long page){
        log.info("正在获取所有订单页面，id：" + id + " status：" + status);
        JSONObject jsonObject = orderService.getOrderList(id,keyword,status,cnt,page);
        log.info("获取所有订单页面成功");
        return ResultUtils.getResult(jsonObject,"success");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "fromId",value = "卖家id",required = true,dataType = "string",paramType = "query"),
    })
    @ApiOperation(value = "卖家已发货",notes = "existWrong：订单不存在或已被冻结 statusWrong：订单状态有误 success：成功")
    @PostMapping("/sendOrder")
    public Result<JSONObject> sendOrder(@RequestParam("number") Long number,@RequestParam("fromId") String fromId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在确认发货，用户：" + fromId + " 订单：" + number);
        String status = orderService.sendOrder(number,fromId);
        return ResultUtils.getResult(jsonObject,status);
    }




    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromId",value = "卖家id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "toId",value = "买家",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "number",value = "订单id",required = true,dataType = "Long",paramType = "query"),
    })
    @ApiOperation(value = "确认收货",notes = "userWrong：买家id或卖家id不正确 existWrong：订单不存在或被冻结 statusWrong：订单状态有误 success：成功")
    @PostMapping("/confirmOrder")
    public Result<JSONObject> confirmOrder(@RequestParam("fromId") String fromId,@RequestParam("toId") String toId,
                                   @RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在确认收货：" + number);
        String status = orderService.confirmOrder(number,fromId,toId);
        return ResultUtils.getResult(jsonObject,status);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "买家id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "number",value = "订单id",required = true,dataType = "Long",paramType = "query"),
    })
    @ApiOperation(value = "取消订单",notes = "existWrong：订单不存在或已被冻结 statusWrong：订单状态有误 success：成功")
    @PostMapping("/cancelOrder")
    public Result<JSONObject> cancelOrder(@RequestParam("id") String id,@RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在取消订单，用户：" + id + " 订单：" + number);
        String status = orderService.cancelOrder(number,id);
        return ResultUtils.getResult(jsonObject,status);
    }


    //申请订单退款
    //退款图片上传待完成
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "退款买家id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "money",value = "退款金额",required = true,dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "reason",value = "退款原因",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "description",value = "描述",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "photo",value = "描述图片url",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "订单退款(付款和已发货状态可用)，通知管理员",notes = "existWrong：订单不存在或已被冻结 statusWrong：订单状态有误 success：成功")
    @PostMapping("/refund")
    public Result<JSONObject> postRefund(@RequestParam("number") Long number,@RequestParam("id") String id,
                             @RequestParam("money") Double money,@RequestParam("reason") String reason,
                             @RequestParam(value = "photo",required = false) String photo,
                                 @RequestParam("description") String description){
        JSONObject jsonObject = new JSONObject();
        log.info("正在生成订单退款：" + number);
        String status = orderService.saveRefund(number,id,money,reason,photo,description);
        return ResultUtils.getResult(jsonObject,status);
    }


    @ApiImplicitParam(name = "photo",value = "描述图片文件",required = true,dataType = "file",paramType = "body")
    @ApiOperation(value = "上传退款描述图片",notes = "fileWrong：上传文件为空 typeWrong：文件类型有误 success：成功 成功返回json url：图片url")
    @PostMapping("/refundPhoto")
    public Result<JSONObject> refundPhotoUpload(@RequestParam("photo") MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        log.info("正在上传退款描述图片");
        String status = orderService.refundPhotoUpload(file);
        if(status.length() > 12){
            //返回的是url
            jsonObject.put("url",status);
            return ResultUtils.getResult(jsonObject,"success");
        }
        return ResultUtils.getResult(jsonObject,status);
    }

}