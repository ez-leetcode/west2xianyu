package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.service.GoodsService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "闲置物品控制类",protocols = "https")
@Slf4j
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;


    //浏览自己的商品
    @ApiOperation(value = "获取闲置物品详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "访问者id",required = true,paramType = "string")
    })
    @GetMapping("/goods")
    public JSONObject getGoods(@RequestParam("number") Long number,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在获取闲置物品详细信息");
        Goods goods = goodsService.getGoods(number,id);
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
        jsonObject.put("Goods",goods);
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

    @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,paramType = "long")
    @ApiOperation(value = "下架自己原有的闲置物品")
    @DeleteMapping("/goods")
    public JSONObject deleteGoods(@RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在下架闲置物品：" + number);
        String status = goodsService.deleteGoods(number);
        jsonObject.put("deleteGoodsStatus",status);
        return jsonObject;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,paramType = "long")
    })
    @ApiOperation("收藏商品")
    @PostMapping("/favor")
    public JSONObject addFavor(@RequestParam("id") String id,@RequestParam("goodsId") Long goodsId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在添加收藏，id：" + id + " goodsId：" + goodsId);
        String status = goodsService.addFavor(goodsId,id);
        jsonObject.put("addFavorStatus",status);
        return jsonObject;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,paramType = "long")
    })
    @ApiOperation("移除商品收藏")
    @DeleteMapping("/favor")
    public JSONObject deleteFavor(@RequestParam("id") String id,@RequestParam("goodsId") Long goodsId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在移除收藏，id：" + id + " goodsId：" + goodsId);
        String status = goodsService.deleteFavor(goodsId,id);
        jsonObject.put("deleteFavorStatus",status);
        return jsonObject;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,paramType = "long")
    })
    @ApiOperation("获取全部收藏列表")
    @GetMapping("/favor")
    public JSONObject getFavor(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                               @RequestParam("page") Long page){
        log.info("正在获取全部收藏列表：" + id);
        JSONObject jsonObject = goodsService.getAllFavor(id,cnt,page);
        return jsonObject;
    }


    //4.6
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",required = true,paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,paramType = "long")
    })
    @ApiOperation("关键词搜索收藏列表")
    @GetMapping("/searchFavor")
    public JSONObject searchFavor(@RequestParam("id") String id,@RequestParam("keyword") String keyword,
                                  @RequestParam("cnt") Long cnt,@RequestParam("page") Long page){
        log.info("正在获取关键词收藏列表，id：" + id + " keyword：" + keyword);
        JSONObject jsonObject = goodsService.searchFavor(id,keyword,cnt,page);
        return jsonObject;
    }


    //4.7
    @ApiImplicitParams({
            @ApiImplicitParam(name = "low",value = "价格最低",required = true,paramType = "double"),
            @ApiImplicitParam(name = "high",value = "价格最高",required = true,paramType = "double"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页数",required = true,paramType = "long"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",paramType = "string"),
            @ApiImplicitParam(name = "label1",value = "标签1",paramType = "string"),
            @ApiImplicitParam(name = "label2",value = "标签2",paramType = "string"),
            @ApiImplicitParam(name = "label3",value = "标签3",paramType = "string")
    })
    @ApiOperation("主页关键词获取")
    @GetMapping("/searchGoods")
    public JSONObject searchGoods(@RequestParam("cnt") Long cnt,@RequestParam("page") Long page,
                                  @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam(value = "low") Double low, @RequestParam(value = "high") Double high,
                                  @RequestParam(value = "label1",required = false) String label1,
                                  @RequestParam(value = "label2",required = false) String label2,
                                  @RequestParam(value = "label3",required = false) String label3){
        log.info("正在获取主页，keyword：" + keyword + " low：" + low + " high：" + high);
        JSONObject jsonObject = goodsService.searchGoods(keyword,low,high,cnt,page,label1,label2,label3);
        return jsonObject;
    }


}