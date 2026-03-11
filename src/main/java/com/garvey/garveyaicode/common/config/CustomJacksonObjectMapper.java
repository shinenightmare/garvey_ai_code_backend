package com.garvey.garveyaicode.common.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigInteger;

public class CustomJacksonObjectMapper extends ObjectMapper {

    public CustomJacksonObjectMapper() {
        super();
        // 1. 忽略JSON字符串中存在的、但Java对象没有的属性，避免反序列化失败
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 2. 创建一个简单模块
        SimpleModule simpleModule = new SimpleModule();

        // 3. 为Long.class类型注册一个序列化器，将其转换为字符串
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        // 为Long的基本类型long也注册（虽然雪花ID通常是包装类，但这里为了全面）
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // BigInteger也可能很大，同样处理
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);

        // 4. 注册这个自定义模块
        this.registerModule(simpleModule);

        // 5. 注册Java8时间模块（如果你的项目用了LocalDateTime等）
        this.registerModule(new JavaTimeModule());
    }
}
