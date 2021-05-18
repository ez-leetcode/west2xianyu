package com.west2xianyu.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;



//这个阿里云ssl安全证书出了点问题，支付宝异步回调不认可这个证书，暂时https用不了
@Slf4j
//@Configuration
public class HttpsConfig {


    //对http请求添加安全性约束，转换为https请求
   // @Bean
    public TomcatServletWebServerFactory servletContainer(){
        log.info("正在添加安全性约束");
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory(){
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcatServletWebServerFactory.addAdditionalTomcatConnectors(httpConnector());
        return tomcatServletWebServerFactory;
    }


    //监听原来的8082端口，让他们自动转到443
    //@Bean
    public Connector httpConnector(){
        log.info("正在切换443");
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        //监听的端口号
        connector.setPort(8082);
        connector.setSecure(false);
        //监听到后，转到443端口
        connector.setRedirectPort(443);
        return connector;
    }
/*

#https
server:
  #使用https默认端口
  port: 443
  #https加密配置
  ssl:
    #证书路径
    key-store: classpath:rat403.cn.pfx
    #证书密码
    key-store-password: N2sjS3S3
    #证书类型
    key-store-type: PKCS12
    #开启ssl
    enabled: true
     */


}
