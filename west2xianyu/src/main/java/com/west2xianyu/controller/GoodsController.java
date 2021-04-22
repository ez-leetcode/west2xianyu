package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Goods;
import com.west2xianyu.pojo.Result;
import com.west2xianyu.service.GoodsService;
import com.west2xianyu.utils.ResultUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "闲置物品控制类",protocols = "https")
@Slf4j
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    //pass
    @ApiOperation(value = "获取闲置物品详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "访问者id",required = true,dataType = "string",paramType = "query")
    })
    @GetMapping("/goods")
    public Result<JSONObject> getGoods(@RequestParam("number") Long number, @RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在获取闲置物品详细信息");
        Goods goods = goodsService.getGoods(number,id);
        if(goods == null){
            log.warn("获取闲置物品详细信息失败，物品不存在：" + number);
            return ResultUtils.getResult(jsonObject,"existWrong");
        }
        //后面结合权限管理框架时再添加管理员查看冻结物品功能
        if(goods.getIsFrozen() == 1){
            log.warn("获取闲置物品详细信息失败，物品已被冻结，可以通过管理员账号访问");
            return ResultUtils.getResult(jsonObject,"frozenWrong");
        }
        log.info("获取闲置物品详细信息成功，物品：" + goods.toString());
        jsonObject.put("Goods",goods);
        return ResultUtils.getResult(jsonObject,"success");
    }


    //pass
    @ApiImplicitParam(name = "photo",value = "商品图片",required = true,dataType = "file",paramType = "body")
    @ApiOperation(value = "上传商品图片")
    @PostMapping("/goodsPhoto")
    public Result<JSONObject> uploadGoodsPhoto(@RequestParam("photo")MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        log.info("正在上传商品图片");
        String url = goodsService.uploadGoodsPhoto(file);
        jsonObject.put("url",url);
        return ResultUtils.getResult(jsonObject,"success");
    }


    //pass
    @ApiOperation(value = "创建新的闲置物品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromId",value = "卖家id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "price",value = "价格",required = true,dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "goodsName",value = "物品名",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "description",value = "物品描述",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "photo",value = "图片url",dataType = "string",paramType = "query"),  //可以没有图片描述
            @ApiImplicitParam(name = "reason",value = "转手原因",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label1",value = "标签1",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label2",value = "标签2",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label3",value = "标签3",dataType = "string",paramType = "query")
    })
    @PostMapping("/goods")
    public Result<JSONObject> createGoods(@RequestParam("fromId") String fromId,@RequestParam("price") Double price,
                                  @RequestParam("goodsName") String goodsName,@RequestParam("description") String description,
                                  @RequestParam(value = "photo",required = false) String photo,@RequestParam("reason") String reason,
                                  @RequestParam(value = "label1",required = false) String label1,
                                  @RequestParam(value = "label2",required = false) String label2,
                                  @RequestParam(value = "label3",required = false) String label3){
        JSONObject jsonObject = new JSONObject();
        Goods goods = new Goods(null,fromId,price,null,goodsName,reason,description,null,null,photo,label1,label2,label3,null,
                null,null,null,null,null);
        log.info("正在创建新商品：" + goods.toString());
        String status = goodsService.saveGoods(goods);
        return ResultUtils.getResult(jsonObject,status);
    }


    //可能要用户id
    @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "long",paramType = "query")
    @ApiOperation(value = "下架自己原有的闲置物品")
    @DeleteMapping("/goods")
    public Result<JSONObject> deleteGoods(@RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在下架闲置物品：" + number);
        String status = goodsService.deleteGoods(number);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass 可以降价后告诉收藏者，降价
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "商品编号",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "price",value = "价格",dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "goodsName",value = "商品名称",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "description",value = "描述",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "reason",value = "转手原因",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "photo",value = "图片url",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label1",value = "标签1",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label2",value = "标签2",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label3",value = "标签3",dataType = "string",paramType = "query")
    })
    @ApiOperation("修改商品信息")
    @PatchMapping("/goods")
    public Result<JSONObject> changeGoods(@RequestParam("number") Long number,@RequestParam(value = "price",required = false) Double price,
                                  @RequestParam(value = "goodsName",required = false) String goodsName,
                                  @RequestParam(value = "description",required = false) String description,
                                  @RequestParam(value = "reason",required = false) String reason,
                                  @RequestParam(value = "photo",required = false) String photo,
                                  @RequestParam(value = "label1",required = false) String label1,
                                  @RequestParam(value = "label2",required = false) String label2,
                                  @RequestParam(value = "label3",required = false) String label3){
        //注意历史价格
        JSONObject jsonObject = new JSONObject();
        Goods goods = new Goods(number,null,price,null,goodsName,reason,description,null,null,photo,label1,
                label2,label3,null,null,null,null,null,null);
        log.info("正在修改商品信息：" + goods.toString());
        String status = goodsService.changeGoods(goods);
        return ResultUtils.getResult(jsonObject,status);
    }



    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,dataType = "long",paramType = "query")
    })
    @ApiOperation("收藏商品")
    @PostMapping("/favor")
    public Result<JSONObject> addFavor(@RequestParam("id") String id,@RequestParam("goodsId") Long goodsId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在添加收藏，id：" + id + " goodsId：" + goodsId);
        String status = goodsService.addFavor(goodsId,id);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,dataType = "long",paramType = "query")
    })
    @ApiOperation("移除商品收藏")
    @DeleteMapping("/favor")
    public Result<JSONObject> deleteFavor(@RequestParam("id") String id,@RequestParam("goodsId") Long goodsId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在移除收藏，id：" + id + " goodsId：" + goodsId);
        String status = goodsService.deleteFavor(goodsId,id);
        return ResultUtils.getResult(jsonObject,status);
    }

    //失效的获取不到
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "关键词",dataType = "string",paramType = "query")
    })
    @ApiOperation("获取全部收藏列表")
    @GetMapping("/favor")
    public Result<JSONObject> getFavor(@RequestParam("id") String id, @RequestParam("cnt") Long cnt,
                                       @RequestParam("page") Long page,
                                       @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取全部收藏列表：" + id);
        return ResultUtils.getResult(goodsService.getAllFavor(id,cnt,page,keyword),"success");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "关键词",dataType = "string",paramType = "query")
    })
    @ApiOperation("获取收藏失效列表")
    @GetMapping("/favor1")
    public Result<JSONObject> getFavor1(@RequestParam("id") String id, @RequestParam("cnt") Long cnt,
                                        @RequestParam("page") Long page,
                                        @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取失效的收藏列表：" + id);
        return ResultUtils.getResult(goodsService.getAllFavor1(id,cnt,page,keyword),"success");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "关键词",dataType = "string",paramType = "query")
    })
    @ApiOperation("获取收藏降价列表")
    @GetMapping("/favor2")
    public Result<JSONObject> getFavor2(@RequestParam("id") String id, @RequestParam("cnt") Long cnt,
                                        @RequestParam("page") Long page,
                                        @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取降价的收藏列表：" + id);
        return ResultUtils.getResult(goodsService.getAllFavor2(id,cnt,page,keyword),"success");
    }


/*
    //4.6
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",required = true,paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,paramType = "long")
    })
    @ApiOperation("关键词搜索收藏列表")
    @GetMapping("/searchFavor")
    public Result<JSONObject> searchFavor(@RequestParam("id") String id,@RequestParam("keyword") String keyword,
                                  @RequestParam("cnt") Long cnt,@RequestParam("page") Long page){
        log.info("正在获取关键词收藏列表，id：" + id + " keyword：" + keyword);
        return ResultUtils.getResult(goodsService.searchFavor(id,keyword,cnt,page),"success");
    }


 */

    //4.7
    @ApiImplicitParams({
            @ApiImplicitParam(name = "low",value = "价格最低",required = true,dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "high",value = "价格最高",required = true,dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页数",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "fromId",value = "商家id，没有就是主页搜索",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label1",value = "标签1",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label2",value = "标签2",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label3",value = "标签3",dataType = "string",paramType = "query")
    })
    @ApiOperation("主页/商家关键词获取")
    @GetMapping("/searchGoods")
    public Result<JSONObject> searchGoods(@RequestParam("cnt") Long cnt,@RequestParam("page") Long page,
                                  @RequestParam(value = "fromId",required = false) String fromId,
                                  @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam(value = "low") Double low, @RequestParam(value = "high") Double high,
                                  @RequestParam(value = "label1",required = false) String label1,
                                  @RequestParam(value = "label2",required = false) String label2,
                                  @RequestParam(value = "label3",required = false) String label3){
        log.info("正在获取页面，keyword：" + keyword + " low：" + low + " high：" + high);
        return ResultUtils.getResult(goodsService.searchGoods(fromId,keyword,low,high,cnt,page,label1,label2,label3),"success");
    }



    //记得评论和点赞推送
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "long",paramType = "query")
    })
    @ApiOperation("获取该商品下所有用户评论")
    @GetMapping("/comments")
    public Result<JSONObject> getComments(@RequestParam("goodsId") String goodsId,@RequestParam("cnt") Long cnt,
                                          @RequestParam("page") Long page){
        log.info("正在获取所有用户评论及点赞信息：" + goodsId);
        JSONObject jsonObject = goodsService.getComments(goodsId,cnt,page);
        if(jsonObject == null){
            //返回null，说明商品不存在
            return ResultUtils.getResult(new JSONObject(),"existWrong");
        }
        //商品存在
        return ResultUtils.getResult(jsonObject,"success");
    }

}