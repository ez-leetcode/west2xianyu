package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.msg.GoodsMsg2;
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
    @ApiOperation(value = "获取闲置物品详细信息",notes = "existWrong：商品不存在或已被下单（现在未审核的可以看了） frozenWrong：商品已被冻结 success：成功 成功返回json goods：商品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "访问者id",required = true,dataType = "string",paramType = "query")
    })
    @GetMapping("/goods")
    public Result<JSONObject> getGoods(@RequestParam("number") Long number, @RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在获取闲置物品详细信息");
        GoodsMsg2 goods = goodsService.getGoods(number,id);
        if(goods == null){
            log.warn("获取闲置物品详细信息失败，物品不存在：" + number);
            return ResultUtils.getResult(jsonObject,"existWrong");
        }
        log.info("获取闲置物品详细信息成功，物品：" + goods.toString());
        jsonObject.put("goods",goods);
        return ResultUtils.getResult(jsonObject,"success");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "在任何情况下获取商品信息的接口",notes = "existWrong：商品真的不存在  success：成功  成功返回json goods：商品信息（这个接口不会增加页面浏览量，可放心用）")
    @GetMapping("/goodsWhenever")
    public Result<JSONObject> getGoodsWhenever(@RequestParam("number") Long number){
        log.info("正在获取闲置物品详细信息（任何条件下） 商品编号：" + number);
        JSONObject jsonObject = goodsService.getGoodsWhenever(number);
        if(jsonObject == null){
            return ResultUtils.getResult(new JSONObject(),"existWrong");
        }
        return ResultUtils.getResult(jsonObject,"success");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "另外一个获取商品信息的接口（有卖家昵称头像等相关信息）",notes = "existWrong：商品真的不存在  success：成功  成功返回json goods：商品信息（这个接口不会增加页面浏览量，可以放心用）")
    @GetMapping("/goodsWhenever1")
    public Result<JSONObject> getGoodsWhenever1(@RequestParam("number") Long number){
        log.info("正在获取闲置物品相关信息：" + number);
        JSONObject jsonObject = goodsService.getGoodsWhenever1(number);
        if(jsonObject == null){
            return ResultUtils.getResult(new JSONObject(),"existWrong");
        }
        return ResultUtils.getResult(jsonObject,"success");
    }



    //pass
    @ApiImplicitParam(name = "photo",value = "商品图片",required = true,dataType = "file",paramType = "body")
    @ApiOperation(value = "上传商品图片",notes = "typeWrong：上传文件类型有误 fileWrong：上传文件为空 success：成功 成功返回json url：商品图片url")
    @PostMapping("/goodsPhoto")
    public Result<JSONObject> uploadGoodsPhoto(@RequestParam("photo")MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        log.info("正在上传商品图片");
        String url = goodsService.uploadGoodsPhoto(file);
        if(url.length() > 12){
            //上传成功
            jsonObject.put("url",url);
            return ResultUtils.getResult(jsonObject,"success");
        }
        return ResultUtils.getResult(jsonObject,url);
    }


    //pass
    @ApiOperation(value = "创建新的闲置物品",notes = "existWrong：用户不存在或已被冻结 success：成功")
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
    @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query")
    @ApiOperation(value = "下架自己原有的闲置物品",notes = "existWrong：商品不存在（可能是重复请求） success：成功")
    @DeleteMapping("/goods")
    public Result<JSONObject> deleteGoods(@RequestParam("number") Long number){
        JSONObject jsonObject = new JSONObject();
        log.info("正在下架闲置物品：" + number);
        String status = goodsService.deleteGoods(number);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass 可以降价后告诉收藏者，降价
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "商品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "price",value = "价格",dataType = "double",paramType = "query"),
            @ApiImplicitParam(name = "goodsName",value = "商品名称",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "description",value = "描述",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "reason",value = "转手原因",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "photo",value = "图片url",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label1",value = "标签1",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label2",value = "标签2",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label3",value = "标签3",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "修改商品信息",notes = "existWrong：商品不存在或已被冻结 success：成功")
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
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "收藏商品",notes = "existWrong：商品不存在或已被冻结  repeatWrong：商品已被收藏（可能是重复请求） success：成功")
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
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "移除商品收藏",notes = "existWrong：该商品未被收藏（可能是重复请求） success：成功")
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
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "关键词",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "获取全部收藏列表",notes = "success：成功 成功返回json favorList：收藏商品信息列表 pages：页面数 count：总数")
    @GetMapping("/favor")
    public Result<JSONObject> getAllFavor(@RequestParam("id") String id, @RequestParam("cnt") Long cnt,
                                       @RequestParam("page") Long page,
                                       @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取全部收藏列表：" + id);
        return ResultUtils.getResult(goodsService.getAllFavor(id,cnt,page,keyword),"success");
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "关键词",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "获取收藏失效列表",notes = "success：成功 favorList：收藏商品信息列表 pages：页面数 count：总数")
    @GetMapping("/favor1")
    public Result<JSONObject> getFavor1(@RequestParam("id") String id, @RequestParam("cnt") Long cnt,
                                        @RequestParam("page") Long page,
                                        @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取失效的收藏列表：" + id);
        return ResultUtils.getResult(goodsService.getAllFavor1(id,cnt,page,keyword),"success");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "关键词",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "获取收藏降价列表",notes = "success：成功  favorList：收藏商品信息列表 pages：页面数 count：总数")
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
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页数",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "fromId",value = "商家id，没有就是主页搜索",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label1",value = "标签1",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label2",value = "标签2",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "label3",value = "标签3",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "主页/商家关键词获取",notes = "success：成功 成功返回json goodsList：商品信息列表 pages：页面数 count：总数")
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
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取该商品下所有用户评论",notes = "success：成功 成功返回json commentList：评论列表 pages：页面数 count：总数")
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


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取用户拥有的所有闲置物品信息",notes = "success：成功 成功返回json goodsList：商品列表 pages：页面数 count：总数")
    @GetMapping("/goodsList1")
    public Result<JSONObject> getGoodsList(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                           @RequestParam("page") Long page){
        log.info("正在获取该用户所有上架的闲置物品信息：" + id);
        return ResultUtils.getResult(goodsService.getGoodsList(id,cnt,page),"success");
    }


}