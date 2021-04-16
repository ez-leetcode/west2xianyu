package com.west2xianyu.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AlipayService {



    void aliPay(HttpServletResponse response, HttpServletRequest request, String goodsName,Double price,Long number) throws IOException;

    String notifyPay(HttpServletRequest request) throws Exception;

    String refundBill(Long number,Double price) throws Exception;
}
