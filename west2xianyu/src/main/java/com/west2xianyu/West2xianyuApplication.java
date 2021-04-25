package com.west2xianyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


@MapperScan("com.west2xianyu.mapper")
@SpringBootApplication
//redis缓存中间键
@EnableCaching
//配置定时任务
@EnableScheduling
public class West2xianyuApplication {

    public static void main(String[] args) {
        SpringApplication.run(West2xianyuApplication.class, args);
    }

}

//git ls-files | xargs wc -l