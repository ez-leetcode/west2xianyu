package com.west2xianyu.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AlipayService {



    void  aliPay(HttpServletResponse response, HttpServletRequest request) throws IOException;


}
