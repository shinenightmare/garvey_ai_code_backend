package com.garvey.garveyaicode.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 核心组件配置类
 * 声明：交换机（Exchange）、队列（Queue）、绑定关系（Binding）
 */
@Configuration
public class RabbitMQConfig {

    // 1. 定义队列名称常量
    public static final String TEST_QUEUE_NAME = "test.queue";
    // 2. 定义交换机名称常量
    public static final String TEST_EXCHANGE_NAME = "test.exchange";
    // 3. 定义路由键
    public static final String TEST_ROUTING_KEY = "test.routing.key";

    // 声明队列（持久化、非排他、非自动删除）
    @Bean
    public Queue testQueue() {
//        return QueueBuilder.durable(TEST_QUEUE_NAME).build();
        return new Queue(TEST_QUEUE_NAME, true, false, false, null);
    }

    // 声明交换机（直连交换机，最常用）
    @Bean
    public DirectExchange testExchange() {
        return ExchangeBuilder.directExchange(TEST_EXCHANGE_NAME)
                .durable(true) // 持久化
                .build();
    }

    // 绑定队列到交换机（指定路由键）
    @Bean
    public Binding bindingTestQueue(Queue testQueue, DirectExchange testExchange) {
        return BindingBuilder.bind(testQueue)
                .to(testExchange)
                .with(TEST_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        // 创建并配置 DefaultClassMapper
        DefaultClassMapper classMapper = new DefaultClassMapper();

        // 设置信任的包
        classMapper.setTrustedPackages(
                "com.garvey.garveyaicode.model.entity",
                "com.garvey.garveyaicode.dto"
        );

        converter.setClassMapper(classMapper);
        return converter;
    }
}
