package com.west2xianyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;


@MapperScan("com.west2xianyu.mapper")
@SpringBootApplication
public class West2xianyuApplication {

    public static void main(String[] args) {
        SpringApplication.run(West2xianyuApplication.class, args);
    }

}

//git ls-files | xargs wc -l