package com.west2xianyu.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

//接口插件配置
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket(Environment environment){

        //显示swagger的环境
        Profiles profiles = Profiles.of("dev","test");

        //判断当前环境，正式上线时改变环境就可以不显示swagger的页面了
        boolean flag = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(flag)              //是否可以在网页中浏览swagger
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.west2xianyu.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){

        //作者信息
        Contact contact = new Contact("yqc","https://github.com/ez-leetcode/west2xianyu","1006021669@qq.com");

        return new ApiInfo(
                "west2闲鱼",
                "闲鱼接口文档",
                "v1.0",
                "https://github.com/ez-leetcode/west2xianyu",
                contact,
                "Apache 2.0",
                null,
                new ArrayList<>()
        );
    }

}
