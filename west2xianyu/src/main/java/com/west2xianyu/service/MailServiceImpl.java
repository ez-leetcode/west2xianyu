package com.west2xianyu.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class MailServiceImpl implements MailService{

    @Resource
    private JavaMailSender javaMailSender;

    private static final String sender = "1006021669@qq.com";

    @Override
    public void sendEmail(String email, String yzm, String function) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject("西二在线闲鱼验证码");
        //function：业务功能
        message.setText("尊敬的用户，您好：\n"
                + "\n本次" + function + "请求的验证码为：" + yzm + "，该验证码5分钟内有效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n（这是一封自动发送的邮件，不需要回复）");
        javaMailSender.send(message);
    }

    public void sendFrozeEmail(String id, String username, String email, String reason, Date frozenDate, Date openDate){
        //创建SimpleDateFormat类对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
        String frozenDate1 = sdf.format(frozenDate);
        String openDate1 = sdf.format(openDate);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject("西二闲鱼：您的账号因违反社区规定已经被封禁");
        message.setText("尊敬的用户：" + username + "（" + id + "）" + "您好！ \n "
                + "您的账号已于" + frozenDate1 + "被封禁至" + openDate1 + "具体原因如下（如有疑惑请回复此管理员邮件）： \n" + reason);
        javaMailSender.send(message);
    }
}
