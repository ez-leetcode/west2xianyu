package com.west2xianyu.service;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.mysql.cj.util.StringUtils;
import com.west2xianyu.config.AlipayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService{


    @Override
    public void aliPay(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        //初始化Alipay
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl,AlipayConfig.app_id,AlipayConfig.merchant_private_key,"json",AlipayConfig.charset,AlipayConfig.alipay_public_key,AlipayConfig.sign_type);
        //设置请求参数
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();

        alipayTradePagePayRequest.setReturnUrl("http://www.baidu.com");
        alipayTradePagePayRequest.setNotifyUrl("https://openapi.alipaydev.com/gateway.do");

        //商户订单号，后台可以写一个工具类生成一个订单号，必填
        String order_number = "122342131";
        //付款金额，从前台获取，必填
        String total_amount = "201314";
        //订单名称，必填
        String subject = new String("颜芳杰小可爱");
        alipayTradePagePayRequest.setBizContent("{\"out_trade_no\":\"" + order_number + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求
        String result = null;
        try {
            result = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //输出
        PrintWriter printWriter = response.getWriter();
        printWriter.print(result);
        printWriter.flush();
        printWriter.close();
        log.info("返回结果={}",result);
    }
}
