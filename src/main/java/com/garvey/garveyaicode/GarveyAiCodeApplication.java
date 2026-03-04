package com.garvey.garveyaicode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class GarveyAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GarveyAiCodeApplication.class, args);
    }

}
