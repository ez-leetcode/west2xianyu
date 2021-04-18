package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Address;
import com.west2xianyu.pojo.Message;
import com.west2xianyu.pojo.Result;
import com.west2xianyu.pojo.User;
import com.west2xianyu.service.UserService;
import com.west2xianyu.utils.ResultUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@Api(tags = "用户控制类",protocols = "https")
@Slf4j
@RestController
public class UserController {


    @Autowired
    private UserService userService;


    @GetMapping("/test")
    //注释用户名
    public String test(@ApiParam("用户名") User user){
        return "test" + user.toString();
    }


    @ApiOperation(value = "注册帐号请求",notes = "管理员注册请多带一个isAdministrator为1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string"),
            @ApiImplicitParam(name = "password",value = "用户密码",required = true,dataType = "string"),
            @ApiImplicitParam(name = "isAdministrator",value = "是否是管理员",required = true,dataType = "int")

    })
    @ApiResponse(code = 200, message = "返回registerStatus，repeatWrong：用户名重复，verifyWrong：验证码错误，success：成功")
    @PostMapping("/register")
    public Result<JSONObject> register(@RequestParam("id") String id,@RequestParam("password") String password,
                               @RequestParam("isAdministrator") int isAdministrator){
        JSONObject jsonObject = new JSONObject();
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        user.setIsAdministrator(isAdministrator);
        String status = userService.register(user);
        if(status.equals("repeatWrong") || status.equals("verifyWrong")){
            return ResultUtils.getResult(jsonObject,status);
        }
        log.info("注册成功，用户id：" + user.getId());
        return ResultUtils.getResult(jsonObject,"success");
    }


    /*

    @ApiOperation(value = "登录账号请求",notes = "登录成功会返回一个token，接下来登录时需要带上~")
    @PostMapping("/login")
    public JSONObject login(User user){
        System.out.println(111);
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

     */

    @ApiImplicitParams({
            @ApiImplicitParam(name = "photo",value = "头像文件",required = true,paramType = "file"),
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string")
    })
    @ApiOperation(value = "用户上传头像")
    @PostMapping("/userPhoto")
    public Result<JSONObject> uploadPhoto(@RequestParam("photo") MultipartFile file, @RequestParam("id") String id) {
        JSONObject jsonObject = new JSONObject();
        Result<JSONObject> result;
        log.info("正在上传用户头像，id：" + id);
        String status = userService.uploadPhoto(file, id);
        if(status.length() > 12){
            //存的是url
            jsonObject.put("url",status);
            result = ResultUtils.getResult(jsonObject,"success");
        }else{
            result = ResultUtils.getResult(jsonObject,status);
        }
        return result;
    }



    //pass
    @ApiOperation(value = "获取用户信息")
    @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string")
    @GetMapping("/user")
    public Result<JSONObject> getUser(@RequestParam("id") String id){
        log.info("正在获取用户信息，id：" + id);
        JSONObject jsonObject = new JSONObject();
        Result<JSONObject> result;
        User user = userService.getUser(id);
        if(user == null){
            log.warn("获取用户信息失败，用户不存在：" + id);
            result = ResultUtils.getResult(jsonObject,"userWrong");
            return result;
        }
        log.info("获取用户信息成功，用户：" + user.toString());
        jsonObject.put("user",user);
        result = ResultUtils.getResult(jsonObject,"success");
        return result;
    }

    //pass
    @ApiOperation(value = "用于修改界面，保存用户信息",notes = "必带id，可以修改：username,sex,campus,address,email,phone,introduction")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string"),
            @ApiImplicitParam(name = "username",dataType = "string"),
            @ApiImplicitParam(name = "sex",value = "男/女",dataType = "string"),
            @ApiImplicitParam(name = "campus",value = "校区",dataType = "string"),
            @ApiImplicitParam(name = "email",dataType = "string"),
            @ApiImplicitParam(name = "phone",dataType = "string"),
            @ApiImplicitParam(name = "introduction",value = "不超过200字",dataType = "string")
    })
    @PostMapping("/user")
    public Result<JSONObject> saveUser(@RequestParam("id") String id,@RequestParam(value = "username",required = false) String username,
                                       @RequestParam(value = "sex",required = false) String sex,
                                       @RequestParam(value = "campus",required = false) String campus,
                                       @RequestParam(value = "email",required = false) String email,
                                       @RequestParam(value = "phone",required = false) String phone,
                                       @RequestParam(value = "introduction",required = false) String introduction){
        User user = new User(id,username,null,sex,null,email,campus,phone,null,introduction,null, null,
                null,null,null,null,null,null,null,null,null,null,null);
        JSONObject jsonObject = new JSONObject();
        Result<JSONObject> results;
        log.info("正在保存用户信息，id：" + user.getId());
        int result = userService.saveUser(user);
        if(result == 1){
            log.info("修改成功");
            results = ResultUtils.getResult(jsonObject,"success");
        }else{
            log.info("修改失败，可能是提交重复信息所致");
            results = ResultUtils.getResult(jsonObject,"repeatWrong");
        }
        return results;
    }

    //pass
    //删除商品或者冻结时还会有，但是可以点进去，不然异步被下单的商品点进去会有问题
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,type = "string"),
            @ApiImplicitParam(name = "cnt",value = "每页数据量",required = true,type = "long"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,type = "long")
    })
    @ApiOperation(value = "获取用户购物车内容")
    @GetMapping("/shopping")
    public Result<JSONObject> getShopping(@RequestParam("id") String id,@RequestParam("cnt") long cnt,
                                  @RequestParam("page") long page){
        log.info("正在尝试获取用户购物车内容");
        JSONObject jsonObject = userService.getShopping(id,cnt,page);
        //返回信息里，一个shoppingList代表内容，pages代表页面数
        return ResultUtils.getResult(jsonObject,"success");
    }

    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",required = true,paramType = "string")
    })
    @ApiOperation(value = "用户添加闲置物品到购物车",notes = "闲置物品被冻结，不能添加进购物车")
    @PostMapping("/shopping")
    public Result<JSONObject> addShopping(@RequestParam("number") Long number,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试添加进购物车，物品编号：" + number);
        String status = userService.addShopping(number,id);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",required = true,paramType = "string")
    })
    @ApiOperation(value = "用户从购物车移除闲置物品")
    @DeleteMapping("/shopping")
    public Result<JSONObject> deleteShopping(@RequestParam("number") Long number,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试移除出购物车，物品编号：" + number);
        String status = userService.deleteShopping(number,id);
        return ResultUtils.getResult(jsonObject,status);
    }

    //pass
    @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string")
    @ApiOperation(value = "用户清空购物车")
    @DeleteMapping("/deleteAllShopping")
    public Result<JSONObject> deleteAllShopping(@RequestParam("id") String id){
        log.info("正在清空购物车：" + id);
        String status = userService.deleteAllShopping(id);
        return ResultUtils.getResult(new JSONObject(),status);
    }



    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "被关注者id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "fansId",value = "关注者id",required = true,paramType = "string")
    })
    @ApiOperation(value = "添加关注")
    @PostMapping("/fans")
    public Result<JSONObject> addFans(@RequestParam("id") String id,@RequestParam("fansId") String fansId){
        //因为被封号的用户不会显示，所以能加的都是没被封的，不用判断是否被封号
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试添加粉丝，用户：" + id + " 粉丝：" + fansId);
        String status = userService.addFans(id,fansId);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "被关注者id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "fansId",value = "关注者id",required = true,paramType = "string")
    })
    @ApiOperation(value = "取消关注")
    @DeleteMapping("/fans")
    public Result<JSONObject> deleteFans(@RequestParam("id") String id,@RequestParam("fansId") String fansId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试取消关注,用户： " + id + " 粉丝：" + fansId);
        String status = userService.deleteFans(id, fansId);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "一页数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long")
    })
    @ApiOperation(value = "获取粉丝列表")
    @GetMapping("/fans")
    public Result<JSONObject> getFollow(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                @RequestParam("page") Long page){
        log.info("正在获取粉丝列表：" + id);
        JSONObject jsonObject = userService.getFollow(id,cnt,page);
        return ResultUtils.getResult(jsonObject,"success");
    }



    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "评论者id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "comments",value = "用户评论",required = true,paramType = "string")
    })
    @ApiOperation(value = "添加用户评论")
    @PostMapping("/comment")
    public Result<JSONObject> addComment(@RequestParam("goodsId") Long goodsId,@RequestParam("id") String id,
                                 @RequestParam("comments") String comments){
        JSONObject jsonObject = new JSONObject();
        log.info("正在添加用户评论：" + comments);
        String status = userService.addComment(goodsId,id,comments);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "用户学号",required = true,paramType = "string"),
            @ApiImplicitParam(name = "comments",value = "评论",required = true,paramType = "string"),
            @ApiImplicitParam(name = "createTime",value = "评论时间",required = true,type = "Date")
    })
    @ApiOperation(value = "用户自己删除评论",notes = "用户自己才可以删除")
    @DeleteMapping("/comment")
    public Result<JSONObject> deleteComment(@RequestParam("goodsId") Long goodsId, @RequestParam("id") String id,
                                    @RequestParam("comments") String comments, @RequestParam("createTime") String createTime){
        JSONObject jsonObject = new JSONObject();
        log.info("用户正在删除自己的评论：" + comments);
        String status = userService.deleteComment(goodsId,id,comments,createTime);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "goodsId",value = "闲置物品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "comments",value = "用户评论内容",required = true,paramType = "string"),
            @ApiImplicitParam(name = "createTime",value = "评论时间（有可能会出现一个用户评论相同内容）",required = true,paramType = "string")
    })
    @ApiOperation(value = "对评论点赞")
    @PostMapping("/likes")
    public Result<JSONObject> addLikes(@RequestParam("id") String id,@RequestParam("goodsId") Long goodsId,
                               @RequestParam("comments") String comments,@RequestParam("createTime") String createTime){
        JSONObject jsonObject = new JSONObject();
        log.info("正在给评论点赞：" + comments);
        String status = userService.addLikes(goodsId,id,comments,createTime);
        return ResultUtils.getResult(jsonObject,status);
    }

    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "goodsId",value = "闲置物品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "comments",value = "用户评论内容",required = true,paramType = "string"),
            @ApiImplicitParam(name = "createTime",value = "评论时间",required = true,paramType = "string")
    })
    @ApiOperation(value = "取消评论点赞")
    @DeleteMapping("/likes")
    public Result<JSONObject> deleteLikes(@RequestParam("id") String id,@RequestParam("goodsId") Long goodsId,
                                  @RequestParam("comments") String comments,@RequestParam("createTime") String createTime){
        JSONObject jsonObject = new JSONObject();
        log.info("正在取消评论点赞：" + comments);
        String status = userService.deleteLikes(goodsId,id,comments,createTime);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "phone",value = "联系方式（电话）",required = true,paramType = "string"),
            @ApiImplicitParam(name = "title",value = "标题（不超过30字）",required = true,paramType = "string"),
            @ApiImplicitParam(name = "feedbacks",value = "用户反馈（不超过200个字）",required = true,paramType = "string")
    })
    @ApiOperation(value = "添加用户反馈")
    @PostMapping("/feedback")
    public Result<JSONObject> addFeedback(@RequestParam("id") String id,@RequestParam("phone") String phone,
                                  @RequestParam("feedbacks") String feedbacks,@RequestParam("title") String title){
        JSONObject jsonObject = new JSONObject();
        log.info("正在添加用户反馈，用户：" + id + " 反馈：" + feedbacks);
        String status = userService.addFeedback(id,phone,feedbacks,title);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "campus",value = "校区",required = true,paramType = "string"),
            @ApiImplicitParam(name = "realAddress",value = "具体地址",required = true,paramType = "string"),
            @ApiImplicitParam(name = "name",value = "收货人姓名",required = true,paramType = "string"),
            @ApiImplicitParam(name = "phone",value = "电话",required = true,paramType = "string"),
            @ApiImplicitParam(name = "isDefault",value = "是否是默认地址1是0不是",required = true,paramType = "int")
    })
    @ApiOperation(value = "保存用户收货地址")
    @PostMapping("/address")
    public Result<JSONObject> addAddress(@RequestParam("id") String id,@RequestParam("campus") String campus,
                                 @RequestParam("realAddress") String realAddress,@RequestParam("name") String name,
                                 @RequestParam("phone") String phone,@RequestParam("isDefault") int isDefault){
        JSONObject jsonObject = new JSONObject();
        log.info("正在保存用户收获地址信息：" + realAddress);
        String status = userService.addAddress(id,campus,realAddress,name,phone,isDefault);
        return ResultUtils.getResult(jsonObject,status);
    }

    //pass
    //删除默认的地址，会随机转移默认地址设置给其他地址，如没有其他地址，会报错
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "地址编号",required = true,paramType = "string"),
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string")
    })
    @ApiOperation(value = "删除用户的地址配置")
    @DeleteMapping("/address")
    public Result<JSONObject> deleteAddress(@RequestParam("number") Long number,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在删除用户地址信息：" + number);
        String status = userService.deleteAddress(number,id);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "number",value = "用户地址编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "campus",value = "校区",paramType = "string"),
            @ApiImplicitParam(name = "realAddress",value = "具体地址",paramType = "string"),
            @ApiImplicitParam(name = "phone",value = "电话",paramType = "string"),
            @ApiImplicitParam(name = "name",value = "收件人姓名",paramType = "string")
    })
    @ApiOperation("修改用户地址配置")
    @PatchMapping("/address")
    public Result<JSONObject> changeAddress(@RequestParam("id") String id,@RequestParam("number") Long number,
                                    @RequestParam(value = "campus",required = false) String campus,
                                    @RequestParam(value = "realAddress",required = false) String realAddress,
                                    @RequestParam(value = "phone",required = false) String phone,
                                    @RequestParam(value = "name",required = false) String name){
        JSONObject jsonObject = new JSONObject();
        log.info("正在修改用户地址配置，用户：" + id + " 地址编号：" + number);
        String status = userService.changeAddress(new Address(number,id,campus,realAddress,name,phone,null,null));
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,paramType = "long")
    })
    @ApiOperation("获取用户地址列表")
    @GetMapping("/address")
    public Result<JSONObject> getAddress(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                    @RequestParam("page") Long page){
        log.info("正在获取用户地址列表：" + id);
        JSONObject jsonObject = userService.getAddress(id,cnt,page);
        return ResultUtils.getResult(jsonObject,"success");
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId",value = "物品编号",required = true,paramType = "long"),
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string")
    })
    @ApiOperation(value = "用户删除历史记录",notes = "单个删除接口")
    @DeleteMapping("/history")
    public Result<JSONObject> deleteHistory(@RequestParam("goodsId") Long goodsId,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在删除历史记录，商品编号：" + goodsId + " 用户id：" + id);
        String status = userService.deleteHistory(goodsId,id);
        return ResultUtils.getResult(jsonObject,status);
    }



    //4.6到这，可以加HistoryMsg，还不行
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,paramType = "long")
    })
    @ApiOperation(value = "获取用户全部历史记录")
    @GetMapping("/allHistory")
    public Result<JSONObject> getHistory(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                 @RequestParam("page") Long page){
        //待完成
        log.info("正在获取用户全部历史记录：" + id);
        JSONObject jsonObject = userService.getHistory(id,cnt,page);
        return ResultUtils.getResult(jsonObject,"success");
    }

    //pass
    @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string")
    @ApiOperation(value = "清空历史记录")
    @DeleteMapping("/allHistory")
    public Result<JSONObject> deleteAllHistory(@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在清空历史记录，用户id：" + id);
        String status = userService.deleteAllHistory(id);
        return ResultUtils.getResult(jsonObject,status);
    }





    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,paramType = "long"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,paramType = "long"),
            @ApiImplicitParam(name = "isRead",value = "是否已读",required = true,paramType = "int")
    })
    @ApiOperation("获取消息盒子列表内容")
    @GetMapping("/message")
    public Result<JSONObject> getMessage(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                         @RequestParam("page") Long page,@RequestParam("isRead") Integer isRead){
        log.info("正在获取消息盒子内容：" + id);
        return ResultUtils.getResult(userService.getMessage(id,cnt,page,isRead),"success");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string"),
            @ApiImplicitParam(name = "number",value = "消息编号",required = true,paramType = "long")
    })
    @ApiOperation("获取消息具体内容")
    @GetMapping("/getMessage")
    public Result<JSONObject> readMessage(@RequestParam("id") String id,@RequestParam("number") Long number){
        log.info("正在获取消息具体内容：" + number);
        JSONObject jsonObject = userService.getOneMessage(id,number);
        if(jsonObject == null){
            return ResultUtils.getResult(new JSONObject(),"existWrong");
        }
        return ResultUtils.getResult(jsonObject,"success");
    }

    @ApiImplicitParam(name = "id",value = "用户id",required = true,paramType = "string")
    @ApiOperation("将所有消息通知已读")
    @PostMapping("/message")
    public Result<JSONObject> deleteMessage(@RequestParam("id") String id){
        log.info("正在已读所有消息：" + id);
        return ResultUtils.getResult(new JSONObject(),userService.readAllMessage(id));
    }
}