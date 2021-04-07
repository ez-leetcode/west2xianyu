package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.service.AdministratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "管理员控制类",protocols = "https")
@Slf4j
@RestController
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;


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
