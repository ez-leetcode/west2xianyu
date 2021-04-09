package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.User;
import com.west2xianyu.service.AdministratorService;
import com.west2xianyu.service.MailService;
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

@Api(tags = "管理员控制类",protocols = "https")
@Slf4j
@RestController
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private MailService mailService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword",value = "搜索关键词",paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long")
    })
    @ApiOperation("获取所有用户信息")
    @GetMapping("/getUser")
    public JSONObject getAllUser(@RequestParam("cnt") Long cnt,@RequestParam("page") Long page,
                                 @RequestParam(value = "keyword",required = false) String keyword){
        log.info("正在获取所有用户信息");
        return administratorService.getAllUser(keyword,cnt,page);
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "isDeleted",value = "禁用/正常",required = true,paramType = "int"),
            @ApiImplicitParam(name = "keyword",value = "关键词",paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long")
    })
    @ApiOperation("获取正常或者已禁用的用户信息")
    @GetMapping("/getUser1")
    public JSONObject getAllUser1(@RequestParam("isDeleted") int isDeleted,@RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam("cnt") Long cnt,@RequestParam("page") Long page){
        log.info("正在获取用户信息，是否禁用：" + isDeleted);
        return administratorService.getAllUser1(isDeleted,keyword,cnt,page);
    }



    //封禁时长大于999天=永封
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "被封用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "days",value = "封禁时长(天)",required = true,paramType = "int"),
            @ApiImplicitParam(name = "reason",value = "封号原因(发邮件告知)",required = true,paramType = "string")
    })
    @ApiOperation("管理员冻结用户")
    @PostMapping("/frozeUser")
    public JSONObject frozenUser(@RequestParam("id") String id,@RequestParam("days") int days,
                                 @RequestParam("reason") String reason){
        JSONObject jsonObject = new JSONObject();
        log.info("正在封禁账号：" + id + " 时长（天）：" + days);
        User user =  administratorService.frozeUser(id,reason,days);
        if(user == null){
            jsonObject.put("frozeUserStatus","existWrong");
            return jsonObject;
        }
        mailService.sendFrozeEmail(user.getId(),user.getUsername(),user.getEmail(),reason,user.getFrozenDate(),user.getReopenDate());
        log.info("发送封禁提醒邮件成功");
        jsonObject.put("frozeUserStatus","success");
        return jsonObject;
    }





    @ApiImplicitParams({
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "isHide",value = "是否隐藏已读",required = true,paramType = "int")
    })
    @ApiOperation("获取所有用户反馈")
    @GetMapping("/feedbackList")
    public JSONObject getAllFeedback(@RequestParam("cnt") Long cnt,@RequestParam("page") Long page,
                                  @RequestParam("id") String id,@RequestParam("isHide") int isHide){
        log.info("正在获取所有用户反馈：" + id);
        return administratorService.getAllFeedback(id,cnt,page,isHide);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "反馈编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "管理员id",required = true,paramType = "string")
    })
    @ApiOperation("获取单个具体反馈信息")
    @GetMapping("/feedback")
    public JSONObject getFeedback(@RequestParam("number") Long number,@RequestParam("id") String id){
        log.info("正在获取详细反馈信息，管理员：" + id + " 编号：" + number);
        return administratorService.getFeedback(id,number);
    }


}
