package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Orders;
import com.west2xianyu.pojo.Result;
import com.west2xianyu.pojo.User;
import com.west2xianyu.service.*;
import com.west2xianyu.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员控制类",protocols = "https")
@Slf4j
@RestController
//管理员下的接口都要管理员角色
@Secured("ROLE_ADMIN")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private OrderService orderService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取所有用户信息（需要管理员角色）",notes = "success：成功 成功返回json userList：用户信息列表 pages：页面数 count：总数")
    @GetMapping("/getUser")
    public Result<JSONObject> getAllUser(@RequestParam("cnt") Long cnt, @RequestParam("page") Long page,
                             @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取所有用户信息");
        return ResultUtils.getResult(administratorService.getAllUser(keyword,cnt,page),"success");
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "isDeleted",value = "禁用/正常,1禁用 0正常",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "关键词",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取正常或者已禁用的用户信息（需要管理员角色）",notes = "success：成功 成功返回json userList：用户信息列表 pages：页面数 count：总数")
    @GetMapping("/getUser1")
    public Result<JSONObject> getAllUser1(@RequestParam("isDeleted") int isDeleted,@RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam("cnt") Long cnt,@RequestParam("page") Long page){
        log.info("正在获取用户信息，是否禁用：" + isDeleted);
        return ResultUtils.getResult(administratorService.getAllUser1(isDeleted,keyword,cnt,page),"success");
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "被封用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "days",value = "封禁时长(天)",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "reason",value = "封号原因(发邮件告知)",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "管理员冻结用户（需要管理员角色）",notes = "existWrong：用户不存在或已被冻结 success：成功")
    @PostMapping("/frozeUser")
    public Result<JSONObject> frozenUser(@RequestParam("id") String id,@RequestParam("days") int days,
                                 @RequestParam("reason") String reason){
        JSONObject jsonObject = new JSONObject();
        log.info("正在封禁账号：" + id + " 时长（天）：" + days);
        User user = administratorService.frozeUser(id,reason,days);
        if(user == null){
            return ResultUtils.getResult(jsonObject,"existWrong");
        }
        mailService.sendFrozeEmail(user.getId(),user.getUsername(),user.getEmail(),reason,user.getFrozenDate(),user.getReopenDate());
        log.info("发送封禁提醒邮件成功");
        return ResultUtils.getResult(jsonObject,"success");
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "adminId",value = "管理员id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "解封用户（需要管理员角色）",notes = "userWrong：用户未被封号 success：成功")
    @PostMapping("/reopenUser")
    public Result<JSONObject> reopenUser(@RequestParam("id") String id,@RequestParam("adminId") String adminId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在解封用户，用户：" + id + " 管理员：" + adminId);
        String status = administratorService.reopenUser(id,adminId);
        //发送邮件通知用户
        if(status.equals("success")){
            User user = userService.getUser(id);
            mailService.sendReopenEmail(user.getId(),adminId,user.getEmail(),user.getUsername());
        }
        return ResultUtils.getResult(jsonObject,status);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "isPass",value = "是否通过",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "审核商品（需要管理员角色）",notes = "existWrong：商品不存在或已下架 success：成功")
    @PostMapping("/judgeGoods")
    public Result<JSONObject> judgeGoods(@RequestParam("number") Long number,@RequestParam("isPass") int isPass,
                                 @RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在审核商品，管理员：" + id + " 商品：" + number);
        String status = administratorService.judgeGoods(number,id,isPass);
        return ResultUtils.getResult(jsonObject,status);
    }



    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "isHide",value = "是否隐藏已读",required = true,dataType = "int",paramType = "query")
    })
    @ApiOperation(value = "获取所有用户反馈（需要管理员角色）",notes = "success：成功 成功返回json feedbackList：返回信息列表 pages：页面数 count：总数")
    @GetMapping("/feedbackList")
    public Result<JSONObject> getAllFeedback(@RequestParam("cnt") Long cnt, @RequestParam("page") Long page,
                                             @RequestParam("id") String id, @RequestParam("isHide") int isHide){
        log.info("正在获取所有用户反馈：" + id);
        return ResultUtils.getResult(administratorService.getAllFeedback(id,cnt,page,isHide),"success");
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "反馈编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "获取单个具体反馈信息（需要管理员角色）",notes = "existWrong：反馈信息不存在 success：成功 成功返回json feedback：反馈信息")
    @GetMapping("/feedback")
    public Result<JSONObject> getFeedback(@RequestParam("number") Long number,@RequestParam("id") String id){
        log.info("正在获取详细反馈信息，管理员：" + id + " 编号：" + number);
        JSONObject jsonObject = administratorService.getFeedback(id,number);
        if(jsonObject == null){
            //反馈信息不存在
            return ResultUtils.getResult(new JSONObject(),"existWrong");
        }
        return ResultUtils.getResult(administratorService.getFeedback(id,number),"success");
    }




    @ApiImplicitParams({
            @ApiImplicitParam(name = "cnt",value = "每页数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "isPass",value = "是否已经通过审核",required = true,dataType = "int",paramType = "query"),
    })
    @ApiOperation(value = "获取所有商品列表（需要管理员角色）",notes = "success：成功 成功返回json goodsList：商品列表信息  pages：页面数 count：数据量")
    @GetMapping("/goodsList")
    public Result<JSONObject> getGoodsList(@RequestParam("cnt") Long cnt,@RequestParam("page") Long page,
                               @RequestParam(value = "keyword",required = false) String keyword,
                                           @RequestParam("isPass") Integer isPass){
        log.info("正在获取所有订单");
        return ResultUtils.getResult(administratorService.getGoodsList(keyword,cnt,page,isPass),"success");
    }


    //获取退款详细信息待完成
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "获取退款详细信息（需要管理员角色）",notes = "existWrong：订单不存在或没有申请退款 statusWrong：订单状态错误 success：成功 成功返回json refund：退款详细信息")
    @GetMapping("/getRefund")
    public Result<JSONObject> getRefund(@RequestParam("number") Long number,@RequestParam("id") String id){
        log.info("正在获取退款详细信息，管理员：" + id + " 订单：" + number);
        JSONObject jsonObject = administratorService.getRefund(number);
        String status = jsonObject.get("getRefundStatus").toString();
        jsonObject.remove("getRefundStatus");
        return ResultUtils.getResult(jsonObject,status);
    }




    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "isPass",value = "是否同意",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "处理退款申请（需要管理员角色）",notes = "existWrong：订单不存在或没有申请退款 statusWrong：订单状态错误 success：成功")
    @PostMapping("/judgeRefund")
    public Result<JSONObject> judgeRefund(@RequestParam("number") Long number,@RequestParam("isPass") int isPass,
                                   @RequestParam("id") String id) throws Exception{
        JSONObject jsonObject = new JSONObject();
        log.info("正在处理退款：" + number);
        String status = administratorService.judgeRefund(number,id,isPass);
        if(isPass == 1 && status.equals("success")){
            //退款
            Orders orders = orderService.getOrders(number);
            //原价和运费一并退回
            String status1 = alipayService.refundBill(number,orders.getPrice() + orders.getFreight());
            if(status1.equals("success")){
                log.info("退款成功");
            }else{
                log.info("退款失败，原因：" + status1);
                status = status1;
            }
        }
        return ResultUtils.getResult(jsonObject,status);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword",value = "关键词",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取投诉列表（需要管理员角色）",notes = "success：成功  成功返回json complainList：投诉内容列表 pages：页面数 cnt：总数")
    @GetMapping("/complainList")
    public Result<JSONObject> getComplainList(@RequestParam(value = "keyword",required = false) String keyword,
                                              @RequestParam("cnt") Long cnt,@RequestParam("page") Long page){
        log.info("正在获取用户投诉列表");
        JSONObject jsonObject = administratorService.getComplainList(keyword,cnt,page);
        return ResultUtils.getResult(jsonObject,"success");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "投诉编号",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "删除投诉（需要管理员角色）",notes = "existWrong：投诉内容不存在（可能已经被删除） success：成功")
    @DeleteMapping("/complain")
    public Result<JSONObject> deleteComplain(@RequestParam("number") Long number){
        log.info("正在删除投诉内容：" + number);
        String status = administratorService.deleteComplain(number);
        return ResultUtils.getResult(new JSONObject(),status);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId",value = "冻结商品编号",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "冻结商品（需要管理员角色）",notes = "existWrong：商品不存在（可能是重复请求） success：成功")
    @PostMapping("/frozenGoods")
    public Result<JSONObject> frozenGoods(@RequestParam("goodsId") Long goodsId){
        log.info("正在冻结商品：" + goodsId);
        String status = administratorService.frozenGoods(goodsId);
        return ResultUtils.getResult(new JSONObject(),status);
    }

}
