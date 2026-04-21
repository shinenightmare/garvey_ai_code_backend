package com.garvey.garveyaicode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableCaching
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.garvey.garveyaicode.mapper")
public class GarveyAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GarveyAiCodeApplication.class, args);
    }

}
