package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Result;
import com.west2xianyu.pojo.User;
import com.west2xianyu.service.AdministratorService;
import com.west2xianyu.service.MailService;
import com.west2xianyu.service.UserService;
import com.west2xianyu.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "管理员控制类",protocols = "https")
@Slf4j
@RestController
@Secured("ROLE_admin")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long")
    })
    @ApiOperation("获取所有用户信息")
    @GetMapping("/getUser")
    public Result<JSONObject> getAllUser(@RequestParam("cnt") Long cnt, @RequestParam("page") Long page,
                             @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取所有用户信息");
        return ResultUtils.getResult(administratorService.getAllUser(keyword,cnt,page),"success");
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "isDeleted",value = "禁用/正常",required = true,paramType = "int"),
            @ApiImplicitParam(name = "keyword",value = "关键词",paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long")
    })
    @ApiOperation("获取正常或者已禁用的用户信息")
    @GetMapping("/getUser1")
    public Result<JSONObject> getAllUser1(@RequestParam("isDeleted") int isDeleted,@RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam("cnt") Long cnt,@RequestParam("page") Long page){
        log.info("正在获取用户信息，是否禁用：" + isDeleted);
        return ResultUtils.getResult(administratorService.getAllUser1(isDeleted,keyword,cnt,page),"success");
    }



    //封禁时长大于999天=永封
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "被封用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "days",value = "封禁时长(天)",required = true,paramType = "int"),
            @ApiImplicitParam(name = "reason",value = "封号原因(发邮件告知)",required = true,paramType = "string")
    })
    @ApiOperation("管理员冻结用户")
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
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "adminId",value = "管理员id",required = true,paramType = "string")
    })
    @ApiOperation("解封用户")
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
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "isPass",value = "是否通过",required = true,paramType = "int"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,paramType = "string")
    })
    @ApiOperation("审核商品")
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
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "isHide",value = "是否隐藏已读",required = true,paramType = "int")
    })
    @ApiOperation("获取所有用户反馈")
    @GetMapping("/feedbackList")
    public Result<JSONObject> getAllFeedback(@RequestParam("cnt") Long cnt, @RequestParam("page") Long page,
                                             @RequestParam("id") String id, @RequestParam("isHide") int isHide){
        log.info("正在获取所有用户反馈：" + id);
        return ResultUtils.getResult(administratorService.getAllFeedback(id,cnt,page,isHide),"success");
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "反馈编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,paramType = "string")
    })
    @ApiOperation("获取单个具体反馈信息")
    @GetMapping("/feedback")
    public Result<JSONObject> getFeedback(@RequestParam("number") Long number,@RequestParam("id") String id){
        log.info("正在获取详细反馈信息，管理员：" + id + " 编号：" + number);
        return ResultUtils.getResult(administratorService.getFeedback(id,number),"success");
    }




    @ApiImplicitParams({
            @ApiImplicitParam(name = "cnt",value = "每页数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long"),
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",paramType = "string")
    })
    @ApiOperation("获取所有订单")
    @GetMapping("/goodsList")
    public Result<JSONObject> getGoodsList(@RequestParam("cnt") Long cnt,@RequestParam("page") Long page,
                               @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取所有订单");
        return ResultUtils.getResult(administratorService.getGoodsList(keyword,cnt,page),"success");
    }


    //获取退款详细信息待完成
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,paramType = "string")
    })
    @ApiOperation("获取退款详细信息")
    @GetMapping("/getRefund")
    public Result<JSONObject> getRefund(@RequestParam("number") Long number,@RequestParam("id") String id){
        log.info("正在获取退款详细信息，管理员：" + id + " 订单：" + number);
        return ResultUtils.getResult(administratorService.getRefund(number),"success");
    }





    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "订单编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "isPass",value = "是否同意",required = true,paramType = "int"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,paramType = "string")
    })
    @ApiOperation("处理退款申请")
    @PostMapping("/judgeRefund")
    public Result<JSONObject> judgeRefund(@RequestParam("number") Long number,@RequestParam("isPass") int isPass,
                                   @RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在处理退款：" + number);
        String status = administratorService.judgeRefund(number,id,isPass);
        //阿里云退款待完成  4.20
        return ResultUtils.getResult(jsonObject,status);
    }

}
