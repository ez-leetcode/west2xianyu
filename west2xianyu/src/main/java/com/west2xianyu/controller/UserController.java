package com.west2xianyu.controller;


import com.alibaba.fastjson.JSONObject;
import com.west2xianyu.pojo.Address;
import com.west2xianyu.pojo.Result;
import com.west2xianyu.pojo.User;
import com.west2xianyu.service.MailService;
import com.west2xianyu.service.UserService;
import com.west2xianyu.utils.RedisUtils;
import com.west2xianyu.utils.ResultUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.UUID;


@Api(tags = "用户控制类",protocols = "https")
@Slf4j
@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisUtils redisUtils;


    @GetMapping("/test")
    //注释用户名
    public String test(@ApiParam("用户名") User user){
        return "test" + user.toString();
    }


    @ApiOperation(value = "注册帐号请求",notes = "repeatWrong：用户名重复，verifyWrong：验证码错误，success：成功" )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "password",value = "用户密码",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "code",value = "验证码",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "isAdministrator",value = "是否是管理员",required = true,dataType = "int",paramType = "query")
    })
    @PostMapping("/register")
    public Result<JSONObject> register(@RequestParam("id") String id,@RequestParam("password") String password,
                               @RequestParam("isAdministrator") int isAdministrator,@RequestParam("code") String code){
        JSONObject jsonObject = new JSONObject();
        //先判断验证码是否正确
        String yzm = redisUtils.getValue(id + "register");
        log.info("用户输入验证码：" + code);
        log.info("正确验证码：" + yzm);
        if(yzm == null || !yzm.equals(code.toLowerCase())){
            //验证码不正确，直接返回错误
            return ResultUtils.getResult(jsonObject,"codeWrong");
        }
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        user.setIsAdministrator(isAdministrator);
        String status = userService.register(user);
        if(status.equals("repeatWrong")){
            return ResultUtils.getResult(jsonObject,status);
        }
        log.info("注册成功，用户id：" + user.getId());
        return ResultUtils.getResult(jsonObject,"success");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "oldPassword",value = "旧密码",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "newPassword",value = "新密码",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "code",value = "验证码",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "修改密码",notes = "existWrong：用户不存在 codeWrong：验证码错误 oldPasswordWrong：旧密码错误")
    @PostMapping("/changePassword")
    public Result<JSONObject> changePassword(@RequestParam("id") String id,@RequestParam("oldPassword") String oldPassword,
                                             @RequestParam("newPassword") String newPassword,@RequestParam("code") String code){
        log.info("正在修改密码：" + id);
        //先校验验证码是否正确
        String yzm = redisUtils.getValue(id + "changePassword");
        if(yzm == null || !yzm.equals(code.toLowerCase())){
            //验证码不存在或者错误
            log.warn("修改密码失败，验证码错误");
            return ResultUtils.getResult(new JSONObject(),"codeWrong");
        }
        //验证码正确的情况下
        String status = userService.changePassword(id,oldPassword,newPassword);
        return ResultUtils.getResult(new JSONObject(),status);
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "学号",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "newPassword",value = "新密码",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "code",value = "验证码",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "找回密码",notes = "existWrong：用户不存在 codeWrong：验证码错误 success：成功")
    @PostMapping("/findPassword")
    public Result<JSONObject> findPassword(@RequestParam("id") String id,@RequestParam("newPassword") String newPassword,
                                           @RequestParam("code") String code){
        log.info("正在找回密码：" + id);
        //先校验密码是否正确
        String yzm = redisUtils.getValue(id + "findPassword");
        if(yzm == null || !yzm.equals(code.toLowerCase())){
            //验证码不存在或错误
            log.warn("找回密码失败，验证码错误");
            return ResultUtils.getResult(new JSONObject(),"codeWrong");
        }
        //验证码正确的情况下
        String status = userService.findPassword(id,newPassword);
        return ResultUtils.getResult(new JSONObject(),status);
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "status",value = "获取哪一种验证码 1.注册 2.找回密码 3.修改密码",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "获取验证码",notes = "existWrong：用户不存在 success：成功")
    @PostMapping("/code")
    public Result<JSONObject> getMailCode(@RequestParam("status") Integer status,@RequestParam("id") String id){
        log.info("正在获取验证码，状态：" + status);
        String function;
        //获取验证码
        String yzm = UUID.randomUUID().toString().substring(0,5);
        if(status == 1){
            function = "注册";
            //存入数据库
            redisUtils.saveByMinutesTime(id + "register",yzm,15);
            mailService.sendEmail(id + "@fzu.edu.cn",yzm,function);
        }else if(status == 2){
            function = "找回密码";
            User user = userService.getUser(id);
            //无条件获取user，因为有些user已被封号（伪删除）,只能自己写sql获取
            if(user == null){
                log.warn("获取验证码失败，用户不存在：" + id);
                return ResultUtils.getResult(new JSONObject(),"existWrong");
            }
            redisUtils.saveByMinutesTime(id + "findPassword",yzm,15);
            mailService.sendEmail(user.getEmail(),yzm,function);
        }else if(status == 3){
            //修改密码
            function = "修改密码";
            User user = userService.getUser(id);
            if(user == null){
                log.warn("修改密码失败，用户不存在：" + id);
                return ResultUtils.getResult(new JSONObject(),"existWrong");
            }
            redisUtils.saveByMinutesTime(id + "changePassword",yzm,15);
            mailService.sendEmail(user.getEmail(),yzm,function);
        }
        return ResultUtils.getResult(new JSONObject(),"success");
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
            @ApiImplicitParam(name = "photo",value = "头像文件",required = true,dataType = "file",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "用户上传头像",notes = "existWrong：用户不存在 success：成功，成功后json会带头像的url")
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
    @ApiOperation(value = "获取用户信息",notes = "existWrong：用户不存在 success：成功，成功json返回一个user")
    @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string",paramType = "query")
    @GetMapping("/user")
    public Result<JSONObject> getUser(@RequestParam("id") String id){
        log.info("正在获取用户信息，id：" + id);
        JSONObject jsonObject = new JSONObject();
        Result<JSONObject> result;
        User user = userService.getUser(id);
        if(user == null){
            log.warn("获取用户信息失败，用户不存在：" + id);
            result = ResultUtils.getResult(jsonObject,"existWrong");
            return result;
        }
        log.info("获取用户信息成功，用户：" + user.toString());
        jsonObject.put("user",user);
        result = ResultUtils.getResult(jsonObject,"success");
        return result;
    }

    //pass
    @ApiOperation(value = "用于修改界面，保存用户信息",notes = "repeatWrong：重复修改（可能是提交了两次同样的请求） success：成功")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "username",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "sex",value = "男/女",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "campus",value = "校区",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "email",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "phone",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "introduction",value = "不超过200字",dataType = "string",paramType = "query")
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
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "每页数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取用户购物车内容",notes = "返回success：成功  成功返回json数据内有shoppingList：购物车商品列表 pages：页面数 count：总数")
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
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "用户添加闲置物品到购物车",notes = "existWrong：商品不存在（已被下架）frozenWrong：商品已被冻结 repeatWrong：商品已被添加（可能是重复请求）success：成功")
    @PostMapping("/shopping")
    public Result<JSONObject> addShopping(@RequestParam("number") Long number,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试添加进购物车，物品编号：" + number);
        String status = userService.addShopping(number,id);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "用户从购物车移除闲置物品",notes = "existWrong：物品不存在（可能是重复请求） success：成功")
    @DeleteMapping("/shopping")
    public Result<JSONObject> deleteShopping(@RequestParam("number") Long number,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试移除出购物车，物品编号：" + number);
        String status = userService.deleteShopping(number,id);
        return ResultUtils.getResult(jsonObject,status);
    }

    //pass
    @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query")
    @ApiOperation(value = "用户清空购物车",notes = "existWrong：购物车已被清空（可能是重复请求） success：成功")
    @DeleteMapping("/deleteAllShopping")
    public Result<JSONObject> deleteAllShopping(@RequestParam("id") String id){
        log.info("正在清空购物车：" + id);
        String status = userService.deleteAllShopping(id);
        return ResultUtils.getResult(new JSONObject(),status);
    }



    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "被关注者id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "fansId",value = "关注者id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "添加关注",notes = "existWrong：用户已被冻结 repeatWrong：用户已被关注（可能是重复请求） success：成功")
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
            @ApiImplicitParam(name = "id",value = "被关注者id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "fansId",value = "关注者id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "取消关注",notes = "existWrong：用户未被关注（可能是重复请求） success：成功")
    @DeleteMapping("/fans")
    public Result<JSONObject> deleteFans(@RequestParam("id") String id,@RequestParam("fansId") String fansId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在尝试取消关注,用户： " + id + " 粉丝：" + fansId);
        String status = userService.deleteFans(id, fansId);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "一页数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取粉丝列表",notes = "success：成功 成功返回json fansList：粉丝列表 pages：页面数 count：总数")
    @GetMapping("/fans")
    public Result<JSONObject> getFollow(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                @RequestParam("page") Long page){
        log.info("正在获取粉丝列表：" + id);
        JSONObject jsonObject = userService.getFollow(id,cnt,page);
        return ResultUtils.getResult(jsonObject,"success");
    }



    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "评论者id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "comments",value = "用户评论",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "添加用户评论",notes = "frozenWrong：商品不存在或者已经被冻结 userWrong：评论者可能已被封号 success：成功")
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
            @ApiImplicitParam(name = "goodsId",value = "商品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "用户学号",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "comments",value = "评论",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "createTime",value = "评论时间",required = true,dataType = "Date",paramType = "query")
    })
    @ApiOperation(value = "用户自己删除评论",notes = "existWrong：评论不存在（可能是重复请求） success：成功")
    @DeleteMapping("/comment")
    public Result<JSONObject> deleteComment(@RequestParam("goodsId") Long goodsId, @RequestParam("id") String id,
                                    @RequestParam("comments") String comments, @RequestParam("createTime") String createTime){
        JSONObject jsonObject = new JSONObject();
        log.info("用户正在删除自己的评论：" + comments);
        String status = userService.deleteComment(goodsId,id,comments,createTime);
        return ResultUtils.getResult(jsonObject,status);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "查看的用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "number",value = "商品编号",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取商品下面的评论列表",notes = "success：成功  成功返回json commentList：评论列表 pages：页面数 count：总数")
    @GetMapping("/commentList")
    public Result<JSONObject> getCommentList(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                             @RequestParam("page") Long page,@RequestParam("number") Long number){
        log.info("正在获取商品评论列表：" + id);
        return ResultUtils.getResult(userService.getCommentList(id,cnt,page,number),"success");
    }



    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromId",value = "评论用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "likeId",value = "点赞用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "goodsId",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "comments",value = "用户评论内容",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "createTime",value = "评论时间（有可能会出现一个用户评论相同内容）",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "对评论点赞",notes = "existWrong：评论不存在 repeatWrong：评论已被点赞（可能是重复请求） success：成功")
    @PostMapping("/likes")
    public Result<JSONObject> addLikes(@RequestParam("fromId") String fromId,@RequestParam("goodsId") Long goodsId,
                               @RequestParam("comments") String comments,@RequestParam("createTime") String createTime,
                                       @RequestParam("likeId") String likeId){
        JSONObject jsonObject = new JSONObject();
        log.info("正在给评论点赞：" + comments);
        String status = userService.addLikes(goodsId,fromId,likeId,comments,createTime);
        return ResultUtils.getResult(jsonObject,status);
    }

    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "goodsId",value = "闲置物品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "comments",value = "用户评论内容",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "createTime",value = "评论时间",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "取消评论点赞",notes = "existWrong：评论不存在 repeatWrong：点赞已被取消（可能是重复请求） success：成功")
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
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "phone",value = "联系方式（电话）",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "title",value = "标题（不超过30字）",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "feedbacks",value = "用户反馈（不超过200个字）",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "添加用户反馈",notes = "success：成功")
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
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "campus",value = "校区",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "realAddress",value = "具体地址",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "name",value = "收货人姓名",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "phone",value = "电话",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "isDefault",value = "是否是默认地址1是0不是",required = true,dataType = "int",paramType = "query")
    })
    @ApiOperation(value = "保存用户收货地址",notes = "repeatWrong：地址信息重复 existWrong：用户不存在 success：成功")
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
            @ApiImplicitParam(name = "number",value = "地址编号",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "删除用户的地址配置",notes = "existWrong：地址不存在（可能是重复请求） userWrong：用户不存在 addressWrong：地址至少保留一个（不能删） success：成功")
    @DeleteMapping("/address")
    public Result<JSONObject> deleteAddress(@RequestParam("number") Long number,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在删除用户地址信息：" + number);
        String status = userService.deleteAddress(number,id);
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "number",value = "用户地址编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "campus",value = "校区",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "realAddress",value = "具体地址",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "phone",value = "电话",dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "name",value = "收件人姓名",dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "修改用户地址配置",notes = "existWrong：地址设置不存在 success：成功")
    @PatchMapping("/address")
    public Result<JSONObject> changeAddress(@RequestParam("id") String id,@RequestParam("number") Long number,
                                    @RequestParam(value = "campus",required = false) String campus,
                                    @RequestParam(value = "realAddress",required = false) String realAddress,
                                    @RequestParam(value = "phone",required = false) String phone,
                                    @RequestParam(value = "name",required = false) String name){
        JSONObject jsonObject = new JSONObject();
        log.info("正在修改用户地址配置，用户：" + id + " 地址编号：" + number);
        String status = userService.changeAddress(new Address(number,id,campus,realAddress,name,phone,null,null,null));
        return ResultUtils.getResult(jsonObject,status);
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页面",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取用户地址列表",notes = "success：成功 成功返回json addressList：地址列表 pages：页面数 count：总数")
    @GetMapping("/address")
    public Result<JSONObject> getAddress(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                    @RequestParam("page") Long page){
        log.info("正在获取用户地址列表：" + id);
        JSONObject jsonObject = userService.getAddress(id,cnt,page);
        return ResultUtils.getResult(jsonObject,"success");
    }


    //pass
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId",value = "物品编号",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "用户删除单个历史记录",notes = "existWrong：该历史记录不存在（可能是重复删除） success：成功")
    @DeleteMapping("/history")
    public Result<JSONObject> deleteHistory(@RequestParam("goodsId") Long goodsId,@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在删除历史记录，商品编号：" + goodsId + " 用户id：" + id);
        String status = userService.deleteHistory(goodsId,id);
        return ResultUtils.getResult(jsonObject,status);
    }



    //4.6到这，可以加HistoryMsg，还不行
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取用户全部历史记录",notes = "success：成功 成功返回json historyList：历史记录列表 pages：页面数 count：总数")
    @GetMapping("/allHistory")
    public Result<JSONObject> getHistory(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                 @RequestParam("page") Long page){
        //待完成
        log.info("正在获取用户全部历史记录：" + id);
        JSONObject jsonObject = userService.getHistory(id,cnt,page);
        return ResultUtils.getResult(jsonObject,"success");
    }



    //pass
    @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query")
    @ApiOperation(value = "清空历史记录",notes = "existWrong：历史记录不存在（可能是重复请求） success：成功")
    @DeleteMapping("/allHistory")
    public Result<JSONObject> deleteAllHistory(@RequestParam("id") String id){
        JSONObject jsonObject = new JSONObject();
        log.info("正在清空历史记录，用户id：" + id);
        String status = userService.deleteAllHistory(id);
        return ResultUtils.getResult(jsonObject,status);
    }





    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "isRead",value = "是否已读",required = true,dataType = "int",paramType = "query")
    })
    @ApiOperation(value = "获取消息盒子列表内容",notes = "success：成功")
    @GetMapping("/message")
    public Result<JSONObject> getMessage(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                         @RequestParam("page") Long page,@RequestParam("isRead") Integer isRead){
        log.info("正在获取消息盒子内容：" + id);
        return ResultUtils.getResult(userService.getMessage(id,cnt,page,isRead),"success");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "number",value = "消息编号",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取消息具体内容",notes = "existWrong：消息不存在（可能是重复请求） success：成功")
    @GetMapping("/getMessage")
    public Result<JSONObject> readMessage(@RequestParam("id") String id,@RequestParam("number") Long number){
        log.info("正在获取消息具体内容：" + number);
        JSONObject jsonObject = userService.getOneMessage(id,number);
        if(jsonObject == null){
            return ResultUtils.getResult(new JSONObject(),"existWrong");
        }
        return ResultUtils.getResult(jsonObject,"success");
    }

    @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query")
    @ApiOperation(value = "将所有消息通知已读",notes = "existWrong：消息已经全部已读（可能是重复请求） success：成功")
    @PostMapping("/message")
    public Result<JSONObject> deleteMessage(@RequestParam("id") String id){
        log.info("正在已读所有消息：" + id);
        return ResultUtils.getResult(new JSONObject(),userService.readAllMessage(id));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "cnt",value = "页面数据量",required = true,dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前第几页",required = true,dataType = "Long",paramType = "query")
    })
    @ApiOperation(value = "获取用户所有的评价",notes = "success：成功  成功返回json evaluateList：用户评价列表 pages：页面数 count：总数")
    @GetMapping("/evaluateList")
    public Result<JSONObject> getEvaluate(@RequestParam("id") String id,@RequestParam("cnt") Long cnt,
                                          @RequestParam("page") Long page){
        log.info("正在获取用户所有的评价：" + id);
        JSONObject jsonObject = userService.getEvaluate(id,cnt,page);
        return ResultUtils.getResult(jsonObject,"success");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromId",value = "投诉者id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "toId",value = "被投诉者id",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "reason",value = "投诉理由",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "specificReason",value = "具体原因",required = true,dataType = "string",paramType = "query")
    })
    @ApiOperation(value = "投诉用户",notes = "userWrong：用户不存在 success：成功")
    @PostMapping("/complain")
    public Result<JSONObject> complainUser(@RequestParam("fromId") String fromId,@RequestParam("toId") String toId,
                                           @RequestParam("reason") String reason,@RequestParam("specificReason") String specificReason){
        log.info("正在投诉用户，投诉者：" + fromId);
        String status = userService.complainUser(fromId,toId,reason,specificReason);
        return ResultUtils.getResult(new JSONObject(),status);
    }


}