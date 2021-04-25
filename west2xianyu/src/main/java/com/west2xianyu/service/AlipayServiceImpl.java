package com.west2xianyu.service;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.west2xianyu.config.AlipayConfig;
import com.west2xianyu.mapper.OrdersMapper;
import com.west2xianyu.pojo.Message;
import com.west2xianyu.pojo.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService{


    @Autowired
    private OrdersMapper ordersMapper;


    @Override
    public void aliPay(HttpServletResponse response, HttpServletRequest request,String goodsName,Double price,Long number) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        //初始化Alipay
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl,
                AlipayConfig.app_id,
                AlipayConfig.merchant_private_key,
                "json",
                AlipayConfig.charset,
                AlipayConfig.alipay_public_key,
                AlipayConfig.sign_type);
        //设置请求参数
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        log.info("正在生成支付宝订单：" + number);
        //回调地址
        alipayTradePagePayRequest.setReturnUrl(AlipayConfig.return_url);
        //alipayTradePagePayRequest.setNotifyUrl("https://openapi.alipaydev.com/gateway.do");
        alipayTradePagePayRequest.setNotifyUrl(AlipayConfig.notify_url);
        //商户订单号，后台可以写一个工具类生成一个订单号，必填
        String order_number = number.toString();
        //付款金额，从前台获取，必填
        String total_amount = price.toString();
        //订单名称，必填
        //String subject = goodsName;
        alipayTradePagePayRequest.setBizContent("{\"out_trade_no\":\"" + order_number + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + goodsName + "\","
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


    @Override
    public String notifyPay(HttpServletRequest request) throws Exception {
        log.info("正在验证支付是否成功");
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            params.put(name, valueStr);
        }
        //是否认证成功
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
        if(signVerified){
            //交易成功情况下
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            Orders orders = ordersMapper.selectById(out_trade_no);
            log.info("获取订单成功，下面开始更新状态，订单：" + out_trade_no);
            //成功的话会自动更新订单状态
            if(orders != null){
                if(orders.getStatus() == 1){
                    //已付款
                    orders.setStatus(2);
                    ordersMapper.updateById(orders);
                    log.info("更新订单成功：" + orders.toString());
                    //告诉卖家买家已经付款
                    Message message = new Message();
                    message.setId(orders.getFromId());
                    Calendar calendar= Calendar.getInstance();
                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                    message.setTitle("您的订单：" + orders.getNumber() + "已被买家付款，请及时处理");
                    message.setIsRead(0);
                    message.setMsg(dateFormat.format(calendar.getTime()) + " \n" + " 您的订单：" + orders.getNumber() + "已被买家" + orders.getToId() +
                            "成功付款，请及时与买家取得联系");
                }
            }
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            log.info("支付成功，订单号：" + out_trade_no + "支付宝交易号：" + trade_no + "交易状态：" + trade_status);
            //更新订单号
            return "paySuccess";
        }else{
            //交易失败情况下
            log.warn("支付认证失败");
            return "payFail";
        }
    }


    @Override
    public String refundBill(Long number, Double price) throws Exception{
        Orders orders = ordersMapper.selectById(number);
        if(orders == null){
            log.warn("退款失败，订单不存在");
            return "existWrong";
        }
        if(orders.getStatus() != 11){
            log.warn("退款失败，订单状态有误");
            return "statusWrong";
        }
        //初始化Alipay
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl,
                AlipayConfig.app_id,
                AlipayConfig.merchant_private_key,
                "json",
                AlipayConfig.charset,
                AlipayConfig.alipay_public_key,
                AlipayConfig.sign_type);
        //设置请求参数
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        long id = number + 1;
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + number.toString() + "\"," +
                "\"refund_amount\":\"" + price.toString() + "\"," +
                "\"refund_reason\":\"正常退款\"," +
                "\"out_request_no\":\"" + id + "\"}");
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            //退款成功
            log.info("退款成功：" + response.toString());
            return "success";
        }else{
            log.warn("退款失败：" + response.toString());
            return "refundFail";
        }
    }
}
