package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.service.GoodsService;
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

@Api(tags = "闲置物品控制类",protocols = "https")
@Slf4j
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;


    @ApiOperation(value = "获取闲置物品详细信息")
    @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,paramType = "long")
    @GetMapping("/goods")
    public JSONObject getGoods(@RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在获取闲置物品详细信息");
        Goods goods = goodsService.getGoods(number);
        if(goods == null){
            log.warn("获取闲置物品详细信息失败，物品不存在：" + number);
            jsonObject.put("getGoodsStatus","existWrong");
            return jsonObject;
        }
        //后面结合权限管理框架时再添加管理员查看冻结物品功能
        if(goods.getIsFrozen() == 1){
            log.warn("获取闲置物品详细信息失败，物品已被冻结，可以通过管理员账号访问");
        }
        log.info("获取闲置物品详细信息成功，物品：" + goods.toString());
        jsonObject.put("getGoodsStatus",goods);
        return jsonObject;
    }

    @ApiOperation(value = "创建新的闲置物品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromId",value = "卖家id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "price",value = "价格",required = true,paramType = "double"),
            @ApiImplicitParam(name = "goodsName",value = "物品名",required = true,paramType = "string"),
            @ApiImplicitParam(name = "description",value = "物品描述",required = true,paramType = "string"),
            @ApiImplicitParam(name = "label1",value = "标签1",paramType = "string"),
            @ApiImplicitParam(name = "label2",value = "标签2",paramType = "string"),
            @ApiImplicitParam(name = "label3",value = "标签3",paramType = "string")
    })
    @PostMapping("/goods")
    public JSONObject createGoods(Goods goods){
        JSONObject jsonObject = new JSONObject();
        log.info("正在创建新商品：" + goods.toString());
        String status = goodsService.saveGoods(goods);
        if(status.equals("success")){
            jsonObject.put("createGoodsStatus","success");
        }
        return jsonObject;
    }
}